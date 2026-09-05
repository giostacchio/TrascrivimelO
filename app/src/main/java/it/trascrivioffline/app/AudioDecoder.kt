package it.trascrivioffline.app

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Build
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

internal data class DecodedAudio(
    val pcmFile: File,
    val sampleCount: Long,
    val durationSeconds: Double,
)

internal class AudioDecoder(private val context: Context) {
    companion object {
        const val TARGET_SAMPLE_RATE = 16_000
        private const val TIMEOUT_US = 10_000L
    }

    fun decode(
        uri: Uri,
        destination: File,
        cancelled: AtomicBoolean,
        onProgress: (Int) -> Unit,
    ): DecodedAudio {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        val sink = Pcm16Sink(destination)

        try {
            extractor.setDataSource(context, uri, null)
            var trackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val mime = extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    trackIndex = i
                    break
                }
            }
            require(trackIndex >= 0) { "Il file non contiene una traccia audio leggibile." }

            extractor.selectTrack(trackIndex)
            val inputFormat = extractor.getTrackFormat(trackIndex)
            val mime = inputFormat.getString(MediaFormat.KEY_MIME)
                ?: error("Formato audio non riconosciuto.")
            val durationUs = if (inputFormat.containsKey(MediaFormat.KEY_DURATION)) {
                inputFormat.getLong(MediaFormat.KEY_DURATION)
            } else -1L

            // Important on recent Android/Pixel devices: ask MediaCodec for PCM16.
            // If the codec chooses another PCM format anyway, we read the actual
            // output format below and convert it correctly instead of assuming 16-bit.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputFormat.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
            }

            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(inputFormat, null, null, 0)
            codec.start()

            var channels = inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT).coerceAtLeast(1)
            var sampleRate = inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            var pcmEncoding = AudioFormat.ENCODING_PCM_16BIT
            var resampler = StreamingLinearResampler(sampleRate, TARGET_SAMPLE_RATE) { sink.write(it) }

            val info = MediaCodec.BufferInfo()
            var inputEnded = false
            var outputEnded = false
            var lastProgress = -1

            while (!outputEnded) {
                if (cancelled.get()) throw CancellationException()

                if (!inputEnded) {
                    val inputIndex = codec.dequeueInputBuffer(TIMEOUT_US)
                    if (inputIndex >= 0) {
                        val input = codec.getInputBuffer(inputIndex)
                            ?: error("Impossibile leggere il buffer audio.")
                        input.clear()
                        val size = extractor.readSampleData(input, 0)
                        if (size < 0) {
                            codec.queueInputBuffer(
                                inputIndex,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                            )
                            inputEnded = true
                        } else {
                            codec.queueInputBuffer(
                                inputIndex,
                                0,
                                size,
                                extractor.sampleTime.coerceAtLeast(0L),
                                0,
                            )
                            extractor.advance()
                        }
                    }
                }

                when (val outputIndex = codec.dequeueOutputBuffer(info, TIMEOUT_US)) {
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val out = codec.outputFormat
                        val newChannels = out.getInteger(MediaFormat.KEY_CHANNEL_COUNT).coerceAtLeast(1)
                        val newRate = out.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                        val newEncoding = if (
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                            out.containsKey(MediaFormat.KEY_PCM_ENCODING)
                        ) {
                            out.getInteger(MediaFormat.KEY_PCM_ENCODING)
                        } else {
                            AudioFormat.ENCODING_PCM_16BIT
                        }

                        ensureSupportedPcmEncoding(newEncoding)

                        if (sink.sampleCount == 0L && newRate != sampleRate) {
                            sampleRate = newRate
                            resampler = StreamingLinearResampler(sampleRate, TARGET_SAMPLE_RATE) { sink.write(it) }
                        }
                        channels = newChannels
                        pcmEncoding = newEncoding
                    }
                    else -> if (outputIndex >= 0) {
                        if (info.size > 0 && (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                            val output = codec.getOutputBuffer(outputIndex)
                                ?: error("Impossibile decodificare il buffer audio.")
                            output.position(info.offset)
                            output.limit(info.offset + info.size)
                            output.order(ByteOrder.nativeOrder())
                            val mono = pcmToMono(output, channels, pcmEncoding)
                            if (mono.isNotEmpty()) resampler.accept(mono)
                        }

                        if (durationUs > 0) {
                            val progress = ((info.presentationTimeUs * 100L) / durationUs)
                                .toInt().coerceIn(0, 100)
                            if (progress != lastProgress) {
                                lastProgress = progress
                                onProgress(progress)
                            }
                        }

                        outputEnded = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                        codec.releaseOutputBuffer(outputIndex, false)
                    }
                }
            }

            resampler.finish()
            sink.close()
            require(sink.sampleCount > 0) { "Il file audio è vuoto." }
            onProgress(100)

            return DecodedAudio(
                pcmFile = destination,
                sampleCount = sink.sampleCount,
                durationSeconds = sink.sampleCount.toDouble() / TARGET_SAMPLE_RATE,
            )
        } catch (t: Throwable) {
            sink.closeQuietly()
            destination.delete()
            throw t
        } finally {
            try { codec?.stop() } catch (_: Throwable) { }
            try { codec?.release() } catch (_: Throwable) { }
            try { extractor.release() } catch (_: Throwable) { }
        }
    }

    private fun ensureSupportedPcmEncoding(encoding: Int) {
        val supported = encoding == AudioFormat.ENCODING_PCM_8BIT ||
            encoding == AudioFormat.ENCODING_PCM_16BIT ||
            encoding == AudioFormat.ENCODING_PCM_FLOAT ||
            encoding == AudioFormat.ENCODING_PCM_24BIT_PACKED ||
            encoding == AudioFormat.ENCODING_PCM_32BIT
        require(supported) {
            "Formato PCM prodotto da Android non supportato (encoding=$encoding)."
        }
    }

    private fun pcmToMono(buffer: ByteBuffer, channels: Int, encoding: Int): FloatArray {
        ensureSupportedPcmEncoding(encoding)
        val ch = channels.coerceAtLeast(1)
        val bytesPerSample = when (encoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 1
            AudioFormat.ENCODING_PCM_16BIT -> 2
            AudioFormat.ENCODING_PCM_24BIT_PACKED -> 3
            AudioFormat.ENCODING_PCM_FLOAT,
            AudioFormat.ENCODING_PCM_32BIT -> 4
            else -> error("Formato PCM non supportato: $encoding")
        }
        val frames = buffer.remaining() / (bytesPerSample * ch)
        if (frames <= 0) return FloatArray(0)

        val out = FloatArray(frames)
        for (frame in 0 until frames) {
            var sum = 0f
            repeat(ch) {
                sum += readOnePcmSample(buffer, encoding)
            }
            out[frame] = (sum / ch).coerceIn(-1f, 1f)
        }
        return out
    }

    private fun readOnePcmSample(buffer: ByteBuffer, encoding: Int): Float = when (encoding) {
        AudioFormat.ENCODING_PCM_8BIT ->
            ((buffer.get().toInt() and 0xff) - 128) / 128f

        AudioFormat.ENCODING_PCM_16BIT ->
            buffer.short / 32768f

        AudioFormat.ENCODING_PCM_FLOAT -> {
            val v = buffer.float
            if (v.isFinite()) v.coerceIn(-1f, 1f) else 0f
        }

        AudioFormat.ENCODING_PCM_24BIT_PACKED -> {
            // Android specifies packed 24-bit PCM bytes in little-endian order.
            val b0 = buffer.get().toInt() and 0xff
            val b1 = buffer.get().toInt() and 0xff
            val b2 = buffer.get().toInt() and 0xff
            var raw = b0 or (b1 shl 8) or (b2 shl 16)
            if ((raw and 0x00800000) != 0) raw = raw or -0x01000000
            (raw / 8388608f).coerceIn(-1f, 1f)
        }

        AudioFormat.ENCODING_PCM_32BIT ->
            (buffer.int.toDouble() / 2147483648.0).toFloat().coerceIn(-1f, 1f)

        else -> error("Formato PCM non supportato: $encoding")
    }
}

private class StreamingLinearResampler(
    sourceRate: Int,
    targetRate: Int,
    private val onSample: (Float) -> Unit,
) {
    private val step = sourceRate.toDouble() / targetRate.toDouble()
    private var nextSourcePosition = 0.0
    private var totalInput = 0L
    private var previousSample = 0f
    private var hasPrevious = false

    fun accept(input: FloatArray) {
        if (input.isEmpty()) return
        val start = totalInput
        val end = start + input.size - 1L

        while (true) {
            val floorIndex = kotlin.math.floor(nextSourcePosition).toLong()
            val ceilIndex = kotlin.math.ceil(nextSourcePosition).toLong()
            if (ceilIndex > end) break
            if (floorIndex < start - 1L) {
                nextSourcePosition = start.toDouble()
                continue
            }

            val a = sampleAt(floorIndex, start, input)
            val b = sampleAt(ceilIndex, start, input)
            val fraction = (nextSourcePosition - floorIndex).toFloat()
            onSample(a + (b - a) * fraction)
            nextSourcePosition += step
        }

        previousSample = input.last()
        hasPrevious = true
        totalInput += input.size
    }

    fun finish() {
        if (hasPrevious && nextSourcePosition <= totalInput.toDouble()) {
            onSample(previousSample)
            nextSourcePosition += step
        }
    }

    private fun sampleAt(index: Long, start: Long, input: FloatArray): Float {
        if (index == start - 1L && hasPrevious) return previousSample
        val local = (index - start).toInt().coerceIn(0, input.lastIndex)
        return input[local]
    }
}

private class Pcm16Sink(file: File) {
    private val output = BufferedOutputStream(FileOutputStream(file), 128 * 1024)
    var sampleCount: Long = 0
        private set
    private var closed = false

    fun write(value: Float) {
        if (closed) return
        val s = (value.coerceIn(-1f, 1f) * 32767f).roundToInt().coerceIn(-32768, 32767)
        output.write(s and 0xff)
        output.write((s shr 8) and 0xff)
        sampleCount++
    }

    fun close() {
        if (closed) return
        closed = true
        output.flush()
        output.close()
    }

    fun closeQuietly() {
        try { close() } catch (_: Throwable) { }
    }
}
