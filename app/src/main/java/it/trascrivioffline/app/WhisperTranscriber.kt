package it.trascrivioffline.app

import android.content.res.AssetManager
import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import java.io.RandomAccessFile
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.sqrt

internal data class TranscriptChunk(
    val startSeconds: Double,
    val endSeconds: Double,
    val text: String,
)

/**
 * v1.4: direct offline transcription with a current sherpa-onnx Whisper model.
 * The recording is decoded to mono PCM 16 kHz and sent to Whisper in bounded
 * chunks. Speaker diarization is deliberately not part of the ASR path.
 */
internal class WhisperTranscriber(private val assets: AssetManager) {
    companion object {
        private const val SAMPLE_RATE = 16_000
        private const val CHUNK_SECONDS = 30
        private const val CHUNK_SAMPLES = SAMPLE_RATE * CHUNK_SECONDS
        private const val MIN_SAMPLES = 4_800 // 0.3 s
        private const val MIN_AUDIBLE_RMS = 0.00020
    }

    fun transcribe(
        audio: DecodedAudio,
        cancelled: AtomicBoolean,
        onProgress: (Int, String) -> Unit,
        onChunk: (TranscriptChunk) -> Unit,
    ): List<TranscriptChunk> {
        checkCancelled(cancelled)
        verifyModelAssets()
        onProgress(22, "Caricamento di Whisper italiano…")

        val recognizer = createRecognizer()
        val results = ArrayList<TranscriptChunk>()
        var maxRms = 0.0
        var maxPeak = 0f

        try {
            val totalChunks = ((audio.sampleCount + CHUNK_SAMPLES - 1) / CHUNK_SAMPLES)
                .toInt().coerceAtLeast(1)

            RandomAccessFile(audio.pcmFile, "r").use { raf ->
                var chunkIndex = 0
                var firstSample = 0L
                while (firstSample < audio.sampleCount) {
                    checkCancelled(cancelled)
                    val count = minOf(CHUNK_SAMPLES.toLong(), audio.sampleCount - firstSample).toInt()
                    if (count < MIN_SAMPLES) break

                    onProgress(
                        (25 + ((chunkIndex * 70.0) / totalChunks).toInt()).coerceIn(25, 94),
                        "Trascrizione ${chunkIndex + 1}/$totalChunks…",
                    )

                    val samples = readPcm16(raf, firstSample, count)
                    val stats = prepareAudio(samples)
                    maxRms = maxOf(maxRms, stats.first)
                    maxPeak = maxOf(maxPeak, stats.second)

                    val stream = recognizer.createStream()
                    val text = try {
                        stream.acceptWaveform(samples, SAMPLE_RATE)
                        recognizer.decode(stream)
                        recognizer.getResult(stream).text.trim()
                    } finally {
                        stream.release()
                    }

                    if (text.isNotBlank()) {
                        val line = TranscriptChunk(
                            startSeconds = firstSample.toDouble() / SAMPLE_RATE,
                            endSeconds = minOf(
                                audio.durationSeconds,
                                (firstSample + count).toDouble() / SAMPLE_RATE,
                            ),
                            text = text,
                        )
                        results.add(line)
                        onChunk(line)
                    }

                    chunkIndex++
                    firstSample += count
                }
            }

            if (results.isEmpty()) {
                if (maxRms < MIN_AUDIBLE_RMS) {
                    error(
                        "L'audio è stato letto, ma il segnale decodificato risulta quasi silenzioso " +
                            "(RMS=${"%.6f".format(maxRms)}, picco=${"%.4f".format(maxPeak)}).",
                    )
                } else {
                    error(
                        "Whisper ha ricevuto l'audio ma non ha prodotto testo " +
                            "(RMS=${"%.6f".format(maxRms)}, picco=${"%.4f".format(maxPeak)}).",
                    )
                }
            }

            onProgress(98, "Completamento del TXT…")
            return results
        } finally {
            recognizer.release()
        }
    }

    private fun createRecognizer(): OfflineRecognizer {
        val whisper = OfflineWhisperModelConfig(
            encoder = "sherpa-onnx-whisper-tiny/tiny-encoder.int8.onnx",
            decoder = "sherpa-onnx-whisper-tiny/tiny-decoder.int8.onnx",
            language = "it",
            task = "transcribe",
            tailPaddings = 0,
            enableTokenTimestamps = false,
            enableSegmentTimestamps = false,
        )

        val model = OfflineModelConfig(
            whisper = whisper,
            numThreads = 2,
            debug = false,
            provider = "cpu",
            modelType = "whisper",
            tokens = "sherpa-onnx-whisper-tiny/tiny-tokens.txt",
        )

        val config = OfflineRecognizerConfig(
            featConfig = FeatureConfig(
                sampleRate = SAMPLE_RATE,
                featureDim = 80,
                dither = 0.0f,
            ),
            modelConfig = model,
            decodingMethod = "greedy_search",
        )

        return OfflineRecognizer(assets, config)
    }

    private fun verifyModelAssets() {
        val required = arrayOf(
            "sherpa-onnx-whisper-tiny/tiny-encoder.int8.onnx",
            "sherpa-onnx-whisper-tiny/tiny-decoder.int8.onnx",
            "sherpa-onnx-whisper-tiny/tiny-tokens.txt",
        )
        for (path in required) {
            try {
                assets.open(path, AssetManager.ACCESS_STREAMING).use { input ->
                    if (input.read() < 0) error("Asset vuoto: $path")
                }
            } catch (t: Throwable) {
                error("Modello offline mancante o illeggibile: $path (${t.message.orEmpty()})")
            }
        }
    }

    /**
     * Removes a tiny DC offset and applies only conservative gain to quiet
     * recordings. Returns RMS and peak after preparation for diagnostics.
     */
    private fun prepareAudio(samples: FloatArray): Pair<Double, Float> {
        if (samples.isEmpty()) return 0.0 to 0f

        var mean = 0.0
        for (v in samples) mean += v.toDouble()
        mean /= samples.size.toDouble()

        var peak = 0f
        var energy = 0.0
        for (i in samples.indices) {
            val centered = (samples[i] - mean.toFloat()).coerceIn(-1f, 1f)
            samples[i] = centered
            peak = maxOf(peak, abs(centered))
            energy += centered.toDouble() * centered.toDouble()
        }

        var rms = sqrt(energy / samples.size.toDouble())
        if (rms >= MIN_AUDIBLE_RMS && peak in 0.001f..0.45f) {
            val gain = minOf(0.85f / peak, 4.0f)
            if (gain > 1.05f) {
                peak = 0f
                energy = 0.0
                for (i in samples.indices) {
                    val v = (samples[i] * gain).coerceIn(-1f, 1f)
                    samples[i] = v
                    peak = maxOf(peak, abs(v))
                    energy += v.toDouble() * v.toDouble()
                }
                rms = sqrt(energy / samples.size.toDouble())
            }
        }
        return rms to peak
    }

    private fun readPcm16(raf: RandomAccessFile, firstSample: Long, count: Int): FloatArray {
        val bytes = ByteArray(count * 2)
        raf.seek(firstSample * 2L)
        raf.readFully(bytes)
        val samples = FloatArray(count)
        var p = 0
        for (i in 0 until count) {
            val lo = bytes[p].toInt() and 0xff
            val hi = bytes[p + 1].toInt()
            val s = (hi shl 8) or lo
            samples[i] = s.toShort() / 32768f
            p += 2
        }
        return samples
    }

    private fun checkCancelled(cancelled: AtomicBoolean) {
        if (cancelled.get()) throw CancellationException()
    }
}
