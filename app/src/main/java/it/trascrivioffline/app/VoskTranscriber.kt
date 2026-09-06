package it.trascrivioffline.app

import android.content.Context
import android.content.res.AssetManager
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean

internal data class TranscriptChunk(
    val startSeconds: Double,
    val endSeconds: Double,
    val text: String,
)

/**
 * Offline Italian transcription powered by Vosk/Kaldi.
 *
 * The input is the mono PCM16/16 kHz file produced by AudioDecoder. Unlike
 * Whisper, Vosk is a streaming speech recognizer: audio is fed continuously
 * and results are emitted at speech boundaries. This avoids the repeated
 * free-form hallucinations we observed with Whisper on long meeting audio.
 */
internal class VoskTranscriber(private val context: Context) {
    companion object {
        private const val SAMPLE_RATE = 16_000f
        private const val MODEL_ASSET_DIR = "vosk-model-small-it-0.22"
        private const val MODEL_DISK_DIR = "vosk-model-small-it-0.22"
        private const val IO_BUFFER = 16 * 1024
    }

    fun transcribe(
        audio: DecodedAudio,
        cancelled: AtomicBoolean,
        onProgress: (Int, String) -> Unit,
        onChunk: (TranscriptChunk) -> Unit,
    ): List<TranscriptChunk> {
        checkCancelled(cancelled)
        onProgress(21, "Preparazione del modello italiano Vosk…")

        val modelDir = ensureModelOnDisk(cancelled)
        checkCancelled(cancelled)
        onProgress(24, "Caricamento del motore Vosk…")

        val results = ArrayList<TranscriptChunk>()
        var lastEnd = 0.0
        val totalBytes = (audio.sampleCount * 2L).coerceAtLeast(1L)
        var consumedBytes = 0L

        Model(modelDir.absolutePath).use { model ->
            Recognizer(model, SAMPLE_RATE).use { recognizer ->
                recognizer.setWords(true)

                BufferedInputStream(FileInputStream(audio.pcmFile), IO_BUFFER * 2).use { input ->
                    val buffer = ByteArray(IO_BUFFER)
                    while (true) {
                        checkCancelled(cancelled)
                        val n = input.read(buffer)
                        if (n <= 0) break

                        consumedBytes += n.toLong()
                        val p = 25 + ((consumedBytes * 70L) / totalBytes).toInt()
                        onProgress(p.coerceIn(25, 95), "Riconoscimento vocale… ${((consumedBytes * 100L) / totalBytes).coerceIn(0, 100)}%")

                        if (recognizer.acceptWaveForm(buffer, n)) {
                            val chunk = parseResult(
                                json = recognizer.result,
                                fallbackStart = lastEnd,
                                fallbackEnd = (consumedBytes / 2.0) / SAMPLE_RATE,
                            )
                            if (chunk != null) {
                                results.add(chunk)
                                lastEnd = maxOf(lastEnd, chunk.endSeconds)
                                onChunk(chunk)
                            }
                        }
                    }
                }

                val finalChunk = parseResult(
                    json = recognizer.finalResult,
                    fallbackStart = lastEnd,
                    fallbackEnd = audio.durationSeconds,
                )
                if (finalChunk != null) {
                    results.add(finalChunk)
                    onChunk(finalChunk)
                }
            }
        }

        if (results.isEmpty()) {
            error("Vosk ha letto l'audio ma non ha riconosciuto parlato italiano affidabile.")
        }

        onProgress(98, "Completamento del TXT…")
        return results
    }

    private fun parseResult(json: String, fallbackStart: Double, fallbackEnd: Double): TranscriptChunk? {
        val obj = JSONObject(json)
        val text = obj.optString("text", "").trim()
        if (text.isBlank()) return null

        var start = fallbackStart.coerceAtLeast(0.0)
        var end = fallbackEnd.coerceAtLeast(start)
        val words = obj.optJSONArray("result")
        if (words != null && words.length() > 0) {
            val first = words.optJSONObject(0)
            val last = words.optJSONObject(words.length() - 1)
            if (first != null) start = first.optDouble("start", start).coerceAtLeast(0.0)
            if (last != null) end = last.optDouble("end", end).coerceAtLeast(start)
        }

        return TranscriptChunk(startSeconds = start, endSeconds = end, text = text)
    }

    /**
     * Vosk requires a normal filesystem directory for its Kaldi model. The
     * model is bundled in APK assets, then copied once into internal app storage.
     */
    private fun ensureModelOnDisk(cancelled: AtomicBoolean): File {
        val target = File(context.filesDir, MODEL_DISK_DIR)
        val marker = File(target, "am/final.mdl")
        val config = File(target, "conf/model.conf")
        if (marker.isFile && marker.length() > 1_000_000L && config.isFile) return target

        target.deleteRecursively()
        copyAssetTree(context.assets, MODEL_ASSET_DIR, target, cancelled)

        require(marker.isFile && marker.length() > 1_000_000L && config.isFile) {
            "Il modello italiano Vosk non è stato copiato correttamente."
        }
        return target
    }

    private fun copyAssetTree(
        assets: AssetManager,
        assetPath: String,
        target: File,
        cancelled: AtomicBoolean,
    ) {
        checkCancelled(cancelled)
        val children = assets.list(assetPath).orEmpty()
        if (children.isEmpty()) {
            target.parentFile?.mkdirs()
            assets.open(assetPath, AssetManager.ACCESS_STREAMING).use { input ->
                FileOutputStream(target).use { output ->
                    val buffer = ByteArray(256 * 1024)
                    while (true) {
                        checkCancelled(cancelled)
                        val n = input.read(buffer)
                        if (n <= 0) break
                        output.write(buffer, 0, n)
                    }
                }
            }
            return
        }

        target.mkdirs()
        for (child in children) {
            copyAssetTree(assets, "$assetPath/$child", File(target, child), cancelled)
        }
    }

    private fun checkCancelled(cancelled: AtomicBoolean) {
        if (cancelled.get()) throw CancellationException()
    }
}
