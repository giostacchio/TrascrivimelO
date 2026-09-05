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

internal data class TranscriptChunk(
    val startSeconds: Double,
    val endSeconds: Double,
    val text: String,
)

/**
 * Transcribes the decoded PCM directly with Whisper.
 *
 * v1.2 first required speaker-diarization to return segments. If diarization
 * returned no segments, Whisper was never called and the TXT contained only
 * the header. This engine deliberately makes ASR independent from diarization:
 * every part of the recording is sent to Whisper in bounded chunks.
 */
internal class WhisperTranscriber(private val assets: AssetManager) {
    companion object {
        private const val SAMPLE_RATE = 16_000
        private const val CHUNK_SECONDS = 25
        private const val CHUNK_SAMPLES = SAMPLE_RATE * CHUNK_SECONDS
        private const val MIN_SAMPLES = 4_800 // 0.3 s
    }

    fun transcribe(
        audio: DecodedAudio,
        cancelled: AtomicBoolean,
        onProgress: (Int, String) -> Unit,
        onChunk: (TranscriptChunk) -> Unit,
    ): List<TranscriptChunk> {
        checkCancelled(cancelled)
        onProgress(22, "Caricamento di Whisper italiano…")

        val recognizer = createRecognizer()
        val results = ArrayList<TranscriptChunk>()
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

                    val progressBefore = 25 + ((chunkIndex * 70.0) / totalChunks).toInt()
                    onProgress(
                        progressBefore.coerceIn(25, 94),
                        "Trascrizione ${chunkIndex + 1}/$totalChunks…",
                    )

                    val samples = readPcm16(raf, firstSample, count)
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
            tailPaddings = 1000,
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
