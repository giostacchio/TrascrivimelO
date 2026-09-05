package it.trascrivioffline.app

import android.content.res.AssetManager
import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import java.io.RandomAccessFile
import java.util.Locale
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
 * v1.5: direct offline transcription with guarded input.
 * Audio is already decoded to real mono PCM16/16 kHz by AudioDecoder.
 * We do not amplify weak/noisy chunks before Whisper: doing that can turn
 * background noise into invented speech. Very quiet chunks are skipped and
 * obvious repeated-output hallucinations are rejected.
 */
internal class WhisperTranscriber(private val assets: AssetManager) {
    companion object {
        private const val SAMPLE_RATE = 16_000
        private const val CHUNK_SECONDS = 30
        private const val CHUNK_SAMPLES = SAMPLE_RATE * CHUNK_SECONDS
        private const val MIN_SAMPLES = 4_800 // 0.3 s
        private const val SILENCE_RMS = 0.0010
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
        var audibleChunks = 0
        var rejectedHallucinations = 0

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

                    // Do not ask Whisper to invent words from near-silence.
                    if (stats.first >= SILENCE_RMS) {
                        audibleChunks++
                        val stream = recognizer.createStream()
                        val text = try {
                            stream.acceptWaveform(samples, SAMPLE_RATE)
                            recognizer.decode(stream)
                            recognizer.getResult(stream).text.trim()
                        } finally {
                            stream.release()
                        }

                        if (text.isNotBlank()) {
                            if (looksLikeRunawayRepetition(text)) {
                                rejectedHallucinations++
                            } else {
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
                        }
                    }

                    chunkIndex++
                    firstSample += count
                }
            }

            if (results.isEmpty()) {
                val rms = String.format(Locale.US, "%.6f", maxRms)
                val peak = String.format(Locale.US, "%.4f", maxPeak)
                when {
                    audibleChunks == 0 -> error(
                        "Il file è stato letto, ma dopo la conversione il segnale risulta quasi silenzioso " +
                            "(RMS=$rms, picco=$peak).",
                    )
                    rejectedHallucinations > 0 -> error(
                        "Whisper stava producendo testo ripetitivo non affidabile e l'app lo ha bloccato. " +
                            "Audio ricevuto: RMS=$rms, picco=$peak.",
                    )
                    else -> error(
                        "Whisper ha ricevuto audio reale ma non ha prodotto testo " +
                            "(RMS=$rms, picco=$peak).",
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
            tailPaddings = 300,
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

    /** Remove DC only. Do not amplify noise before ASR. */
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
        return sqrt(energy / samples.size.toDouble()) to peak
    }

    /**
     * Blocks the classic Whisper failure where one sentence is repeated over
     * and over. It does not alter normal text; it only rejects a chunk when the
     * same substantial clause appears at least three times.
     */
    private fun looksLikeRunawayRepetition(text: String): Boolean {
        val parts = text
            .split(Regex("[.!?;\\n]|\\s+-\\s+"))
            .map { it.trim().lowercase(Locale.ITALY).replace(Regex("\\s+"), " ") }
            .filter { it.length >= 18 && it.split(' ').size >= 4 }
        if (parts.size < 3) return false
        val counts = HashMap<String, Int>()
        for (part in parts) {
            val n = (counts[part] ?: 0) + 1
            counts[part] = n
            if (n >= 3) return true
        }
        return false
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
