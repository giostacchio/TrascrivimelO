package it.trascrivioffline.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : Activity() {
    companion object {
        private const val REQUEST_AUDIO = 1001
        private const val REQUEST_SAVE = 1002
        private const val PREVIEW_LIMIT = 120_000
        private const val RECOVERY_FILE = "trascrizione_recupero_vosk.txt"
    }

    private lateinit var chooseButton: Button
    private lateinit var startButton: Button
    private lateinit var cancelButton: Button
    private lateinit var copyButton: Button
    private lateinit var saveButton: Button
    private lateinit var fileView: TextView
    private lateinit var statusView: TextView
    private lateinit var resultView: TextView
    private lateinit var progressBar: ProgressBar

    private val worker: ExecutorService = Executors.newSingleThreadExecutor()
    private val cancelled = AtomicBoolean(false)
    private var running = false
    private var selectedUri: Uri? = null
    private var selectedName = ""
    private var transcript = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(28, 45, 54)
        window.navigationBarColor = Color.rgb(28, 45, 54)
        createInterface()
        restoreRecovery()
    }

    override fun onDestroy() {
        cancelled.set(true)
        worker.shutdownNow()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android API but kept for minSdk 23 compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return

        when (requestCode) {
            REQUEST_AUDIO -> data.data?.let { selectAudio(it) }
            REQUEST_SAVE -> data.data?.let { uri ->
                try {
                    contentResolver.openOutputStream(uri, "w")?.use { out ->
                        OutputStreamWriter(out, Charsets.UTF_8).use { it.write(transcript) }
                    } ?: error("Impossibile aprire il file scelto.")
                    Toast.makeText(this, "TXT salvato", Toast.LENGTH_LONG).show()
                } catch (t: Throwable) {
                    Toast.makeText(this, "Salvataggio non riuscito: ${friendlyError(t)}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createInterface() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(28))
            setBackgroundColor(Color.rgb(246, 245, 240))
        }

        page.addView(text("Trascrivimelo", 28f, Color.rgb(28, 45, 54), true))
        page.addView(text("Trascrizione italiana offline — motore Vosk, nessun credito e nessun servizio esterno", 14f, Color.rgb(80, 86, 88)).apply {
            setPadding(0, dp(4), 0, dp(18))
        })

        chooseButton = button("1. Scegli registrazione") { openAudioPicker() }
        page.addView(chooseButton)

        fileView = text("Nessun file selezionato", 14f, Color.rgb(100, 103, 104)).apply {
            setPadding(dp(4), dp(10), dp(4), dp(16))
        }
        page.addView(fileView)

        startButton = button("2. Trascrivi ora") { startTranscription() }.apply { isEnabled = false }
        page.addView(startButton)

        cancelButton = button("Interrompi") {
            cancelled.set(true)
            statusView.text = "Interruzione in corso…"
            cancelButton.isEnabled = false
        }.apply { visibility = View.GONE }
        page.addView(cancelButton)

        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
            visibility = View.GONE
        }
        page.addView(progressBar, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(14)).apply {
            setMargins(0, dp(18), 0, dp(8))
        })

        statusView = text("Pronto", 14f, Color.rgb(36, 104, 89), true)
        page.addView(statusView)

        resultView = text("Qui comparirà il testo riconosciuto dalla registrazione.", 15f, Color.rgb(48, 52, 53)).apply {
            setTextIsSelectable(true)
            setLineSpacing(0f, 1.15f)
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(Color.WHITE)
        }
        page.addView(resultView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, dp(14), 0, 0)
        })

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        copyButton = button("Copia testo") { copyTranscript() }.apply { isEnabled = false }
        saveButton = button("Salva TXT") { openSavePicker() }.apply { isEnabled = false }
        actions.addView(copyButton, LinearLayout.LayoutParams(0, dp(54), 1f).apply { setMargins(0, dp(12), dp(5), 0) })
        actions.addView(saveButton, LinearLayout.LayoutParams(0, dp(54), 1f).apply { setMargins(dp(5), dp(12), 0, 0) })
        page.addView(actions)

        page.addView(text("Versione 2.0: motore Vosk/Kaldi italiano. Whisper è stato rimosso completamente.", 12f, Color.rgb(105, 108, 108)).apply {
            setPadding(0, dp(20), 0, 0)
        })

        val scroll = ScrollView(this)
        scroll.addView(page)
        setContentView(scroll)
    }

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_AUDIO)
    }

    private fun selectAudio(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) { }

        selectedUri = uri
        selectedName = displayName(uri)
        transcript = ""
        File(filesDir, RECOVERY_FILE).delete()
        fileView.text = selectedName
        fileView.setTextColor(Color.rgb(36, 104, 89))
        resultView.text = "File pronto. Tocca “Trascrivi ora”."
        statusView.text = "Pronto"
        startButton.isEnabled = true
        copyButton.isEnabled = false
        saveButton.isEnabled = false
    }

    private fun startTranscription() {
        val uri = selectedUri ?: return
        if (running) return

        running = true
        cancelled.set(false)
        transcript = ""
        setRunningUi(true)
        progressBar.progress = 1
        statusView.text = "Preparazione dell’audio…"
        resultView.text = "Conversione e riconoscimento in corso. Tieni l’app aperta."
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        worker.execute {
            var temp: File? = null
            try {
                temp = File.createTempFile("trascrivimelo-", ".pcm", cacheDir)
                val decoded = AudioDecoder(this).decode(uri, temp, cancelled) { p ->
                    updateProgress(2 + (p * 18 / 100), "Conversione audio… $p%")
                }
                if (cancelled.get()) throw CancellationException()

                val recovery = File(filesDir, RECOVERY_FILE)
                val header = buildHeader(decoded.durationSeconds)
                recovery.writeText(header, Charsets.UTF_8)
                transcript = header

                val chunks = VoskTranscriber(this).transcribe(
                    audio = decoded,
                    cancelled = cancelled,
                    onProgress = { p, message -> updateProgress(p, message) },
                    onChunk = { chunk ->
                        val line = formatChunk(chunk)
                        recovery.appendText(line, Charsets.UTF_8)
                        transcript += line
                        runOnUiThread { resultView.text = preview(transcript) }
                    },
                )

                require(chunks.isNotEmpty()) { "Nessun parlato italiano riconosciuto." }
                val complete = "\n--- Trascrizione completata ---\n"
                recovery.appendText(complete, Charsets.UTF_8)
                transcript += complete

                val autoPath = autoSaveTxt(transcript, selectedName)
                runOnUiThread {
                    progressBar.progress = 100
                    statusView.text = "Completato. TXT salvato in $autoPath"
                    resultView.text = preview(transcript)
                    copyButton.isEnabled = true
                    saveButton.isEnabled = true
                }
            } catch (_: CancellationException) {
                val partial = File(filesDir, RECOVERY_FILE)
                transcript = if (partial.isFile) partial.readText(Charsets.UTF_8) else transcript
                runOnUiThread {
                    statusView.text = "Interrotta. Il testo già prodotto è stato mantenuto."
                    resultView.text = if (transcript.isBlank()) "Nessun testo prodotto." else preview(transcript)
                    copyButton.isEnabled = transcript.isNotBlank()
                    saveButton.isEnabled = transcript.isNotBlank()
                    progressBar.progress = 0
                }
            } catch (t: Throwable) {
                val partial = File(filesDir, RECOVERY_FILE)
                if (partial.isFile && partial.length() > 0) transcript = partial.readText(Charsets.UTF_8)
                runOnUiThread {
                    statusView.text = "Trascrizione non completata."
                    resultView.text = friendlyError(t) + if (transcript.isNotBlank()) {
                        "\n\nTesto recuperato:\n\n${preview(transcript)}"
                    } else ""
                    copyButton.isEnabled = transcript.isNotBlank()
                    saveButton.isEnabled = transcript.isNotBlank()
                    progressBar.progress = 0
                }
            } finally {
                temp?.delete()
                runOnUiThread {
                    setRunningUi(false)
                    window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun buildHeader(durationSeconds: Double): String {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY).format(Date())
        return buildString {
            append("TRASCRIVIMELO — Trascrizione offline\n")
            append("File: ").append(selectedName).append('\n')
            append("Data: ").append(date).append('\n')
            append("Durata: ").append(formatTime(durationSeconds)).append("\n\n")
        }
    }

    private fun formatChunk(chunk: TranscriptChunk): String =
        "[${formatTime(chunk.startSeconds)} - ${formatTime(chunk.endSeconds)}] ${chunk.text.trim()}\n\n"

    private fun formatTime(seconds: Double): String {
        val total = seconds.toInt().coerceAtLeast(0)
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        return if (h > 0) String.format(Locale.ITALY, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.ITALY, "%02d:%02d", m, s)
    }

    private fun updateProgress(progress: Int, status: String) {
        runOnUiThread {
            progressBar.progress = progress.coerceIn(0, 100)
            statusView.text = status
        }
    }

    private fun setRunningUi(active: Boolean) {
        chooseButton.isEnabled = !active
        startButton.isEnabled = !active && selectedUri != null
        cancelButton.visibility = if (active) View.VISIBLE else View.GONE
        cancelButton.isEnabled = active
        progressBar.visibility = if (active) View.VISIBLE else View.GONE
        if (!active) running = false
    }

    private fun copyTranscript() {
        if (transcript.isBlank()) return
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Trascrizione", transcript))
        Toast.makeText(this, "Testo copiato", Toast.LENGTH_SHORT).show()
    }

    private fun openSavePicker() {
        if (transcript.isBlank()) return
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, outputFileName(selectedName))
        }
        startActivityForResult(intent, REQUEST_SAVE)
    }

    private fun autoSaveTxt(text: String, sourceName: String): String {
        val fileName = outputFileName(sourceName)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Trascrivimelo")
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Impossibile creare il TXT in Download.")
            contentResolver.openOutputStream(uri, "w")?.use { out ->
                OutputStreamWriter(out, Charsets.UTF_8).use { it.write(text) }
            } ?: error("Impossibile scrivere il TXT in Download.")
            "Download/Trascrivimelo/$fileName"
        } else {
            val dir = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Trascrivimelo")
            dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { out -> OutputStreamWriter(out, Charsets.UTF_8).use { it.write(text) } }
            file.absolutePath
        }
    }

    private fun outputFileName(sourceName: String): String {
        val base = sourceName.substringBeforeLast('.').ifBlank { "registrazione" }
            .replace(Regex("[^A-Za-z0-9À-ÿ._ -]"), "_")
            .take(80)
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "${base}_trascrizione_$stamp.txt"
    }

    private fun displayName(uri: Uri): String {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val i = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (i >= 0) return cursor.getString(i) ?: "registrazione"
            }
        }
        return uri.lastPathSegment ?: "registrazione"
    }

    private fun restoreRecovery() {
        val file = File(filesDir, RECOVERY_FILE)
        if (!file.isFile || file.length() == 0L) return
        try {
            transcript = file.readText(Charsets.UTF_8)
            resultView.text = preview(transcript)
            statusView.text = "Recuperata l’ultima trascrizione Vosk"
            copyButton.isEnabled = true
            saveButton.isEnabled = true
        } catch (_: Throwable) { }
    }

    private fun preview(text: String): String = if (text.length <= PREVIEW_LIMIT) text else
        "… anteprima finale (testo completo nel TXT) …\n\n" + text.takeLast(PREVIEW_LIMIT)

    private fun friendlyError(t: Throwable): String {
        val raw = t.message.orEmpty()
        return when {
            t is OutOfMemoryError -> "Memoria insufficiente. Chiudi le altre app e riprova."
            raw.contains("Vosk", true) || raw.contains("modello", true) -> "Problema nel motore offline italiano: $raw"
            raw.contains("PCM", true) || raw.contains("audio", true) -> "Problema nella lettura dell'audio: $raw"
            raw.isNotBlank() -> raw
            else -> "Errore imprevisto durante la trascrizione."
        }
    }

    private fun button(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 15f
        setOnClickListener { action() }
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
