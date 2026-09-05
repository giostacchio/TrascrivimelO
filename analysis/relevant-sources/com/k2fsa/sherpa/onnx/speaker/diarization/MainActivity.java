package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.ConstantsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000¤\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0006\u0018\u0000 Y2\u00020\u0001:\u0001YB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u001eH\u0002J\u0010\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u0014H\u0002J\u0010\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020\u001eH\u0002J\u0010\u0010&\u001a\u00020\u00122\u0006\u0010'\u001a\u00020(H\u0002J\u0010\u0010)\u001a\u00020\u00122\u0006\u0010*\u001a\u00020+H\u0002J*\u0010,\u001a\u00020\n2\u0006\u0010-\u001a\u00020\u00122\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u00020#2\b\b\u0002\u00101\u001a\u00020#H\u0002J\"\u00102\u001a\u00020\u001e2\u0006\u00103\u001a\u00020#2\u0006\u00104\u001a\u00020#2\b\u00105\u001a\u0004\u0018\u000106H\u0014J\u0012\u00107\u001a\u00020\u001e2\b\u00108\u001a\u0004\u0018\u000109H\u0014J\b\u0010:\u001a\u00020\u001eH\u0014J\b\u0010;\u001a\u00020\u001eH\u0002J\b\u0010<\u001a\u00020\u001eH\u0002J\u0010\u0010=\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u0012H\u0002J\u001e\u0010>\u001a\u00020\u00042\u0006\u0010-\u001a\u00020\u00122\f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u001e0@H\u0002J\b\u0010A\u001a\u00020\u0012H\u0002J\b\u0010B\u001a\u00020\u001eH\u0002J\b\u0010C\u001a\u00020\u001eH\u0002J)\u0010D\u001a\u00020E2\u0006\u0010F\u001a\u00020#2\u0006\u0010G\u001a\u00020#2\n\b\u0002\u0010H\u001a\u0004\u0018\u00010#H\u0002¢\u0006\u0002\u0010IJ\u001a\u0010J\u001a\u00020\u001e2\b\u0010K\u001a\u0004\u0018\u00010L2\u0006\u0010-\u001a\u00020\u0012H\u0002J\u0010\u0010M\u001a\u00020\u001e2\u0006\u0010!\u001a\u00020\u0014H\u0002J\u001e\u0010N\u001a\u00020\u00042\u0006\u0010-\u001a\u00020\u00122\f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u001e0@H\u0002J\u0010\u0010O\u001a\u00020\n2\u0006\u0010-\u001a\u00020\u0012H\u0002J\u0010\u0010P\u001a\u00020\u001e2\u0006\u0010!\u001a\u00020\u0014H\u0002J\b\u0010Q\u001a\u00020\u001eH\u0002J\u0010\u0010R\u001a\u00020\u00122\u0006\u0010S\u001a\u00020TH\u0002J\u0010\u0010U\u001a\u00020\u00122\u0006\u0010V\u001a\u00020TH\u0002J\u0018\u0010W\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020#2\u0006\u0010X\u001a\u00020\u0012H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\nX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u001a\u001a\n \u001c*\u0004\u0018\u00010\u001b0\u001bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006Z"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/MainActivity;", "Landroid/app/Activity;", "()V", "cancelButton", "Landroid/widget/Button;", "cancelled", "Ljava/util/concurrent/atomic/AtomicBoolean;", "chooseButton", "copyButton", "fileView", "Landroid/widget/TextView;", "progressBar", "Landroid/widget/ProgressBar;", "resultView", "running", "", "saveButton", "selectedName", "", "selectedUri", "Landroid/net/Uri;", "speakerSpinner", "Landroid/widget/Spinner;", "startButton", "statusView", "transcript", "worker", "Ljava/util/concurrent/ExecutorService;", "kotlin.jvm.PlatformType", "copyTranscript", "", "createInterface", "displayName", "uri", "dp", "", "value", "endRunningState", "formatTranscriptLine", "line", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptLine;", "friendlyError", "error", "", "label", "text", "size", "", "color", "style", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "openAudioPicker", "openSavePicker", "preview", "primaryButton", "action", "Lkotlin/Function0;", "readRecoveryTranscript", "rememberCurrentAudio", "restoreLastTranscript", "rounded", "Landroid/graphics/drawable/GradientDrawable;", "fill", "radius", "stroke", "(IILjava/lang/Integer;)Landroid/graphics/drawable/GradientDrawable;", "safeAppend", "writer", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/ProgressiveTranscriptWriter;", "saveTranscript", "secondaryButton", "section", "selectAudio", "startTranscription", "time", "seconds", "", "transcriptHeader", "duration", "updateProgress", "status", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final class MainActivity extends Activity {
    private static final String PREFS = "trascrivi_offline";
    private static final String PREF_LAST_AUDIO = "ultimo_audio";
    private static final int PREVIEW_LIMIT = 120000;
    private static final int REQUEST_AUDIO = 1001;
    private static final int REQUEST_SAVE = 1002;
    private Button cancelButton;
    private Button chooseButton;
    private Button copyButton;
    private TextView fileView;
    private ProgressBar progressBar;
    private TextView resultView;
    private boolean running;
    private Button saveButton;
    private Uri selectedUri;
    private Spinner speakerSpinner;
    private Button startButton;
    private TextView statusView;
    private static final int[] SPEAKER_COUNTS = {-1, 2, 3, 4, 5, 6};
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private String selectedName = "";
    private String transcript = "";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(28, 45, 54));
        getWindow().setNavigationBarColor(Color.rgb(28, 45, 54));
        createInterface();
        restoreLastTranscript();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri data2;
        Uri data3;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == REQUEST_AUDIO) {
            if (data == null || (data2 = data.getData()) == null) {
                return;
            }
            selectAudio(data2);
            return;
        }
        if (requestCode != REQUEST_SAVE || data == null || (data3 = data.getData()) == null) {
            return;
        }
        saveTranscript(data3);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.cancelled.set(true);
        this.worker.shutdownNow();
        super.onDestroy();
    }

    private final void createInterface() {
        MainActivity mainActivity = this;
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(20), dp(24), dp(20), dp(40));
        linearLayout.setBackgroundColor(Color.rgb(246, 244, 238));
        TextView textViewLabel = label("TRASCRIVI OFFLINE", 13.0f, Color.rgb(36, 104, 89), 1);
        textViewLabel.setLetterSpacing(0.13f);
        linearLayout.addView(textViewLabel);
        TextView textViewLabel2 = label("Da registrazione a testo, senza cloud", 30.0f, Color.rgb(28, 45, 54), 1);
        textViewLabel2.setPadding(0, dp(8), 0, dp(8));
        linearLayout.addView(textViewLabel2);
        linearLayout.addView(label$default(this, "L’audio resta sul telefono. L’app riconosce l’italiano e divide la conversazione in Persona 1, Persona 2 e le altre voci.", 16.0f, Color.rgb(72, 80, 82), 0, 8, null));
        TextView textViewLabel3 = label("✓  NESSUN INVIO ONLINE  •  NESSUNA CHIAVE  •  NESSUN CREDITO", 12.0f, -1, 1);
        textViewLabel3.setPadding(dp(13), dp(10), dp(13), dp(10));
        textViewLabel3.setBackground(rounded$default(this, Color.rgb(36, 104, 89), 12, null, 4, null));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(0, dp(18), 0, dp(22));
        textViewLabel3.setLayoutParams(layoutParams);
        linearLayout.addView(textViewLabel3);
        linearLayout.addView(section("1. Scegli la registrazione"));
        Button buttonPrimaryButton = primaryButton("Scegli audio (M4A, MP3, WAV…)", new Function0<Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity.createInterface.4
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                MainActivity.this.openAudioPicker();
            }
        });
        this.chooseButton = buttonPrimaryButton;
        if (buttonPrimaryButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("chooseButton");
            buttonPrimaryButton = null;
        }
        linearLayout.addView(buttonPrimaryButton);
        TextView textViewLabel$default = label$default(this, "Nessun file selezionato", 14.0f, Color.rgb(100, 105, 106), 0, 8, null);
        textViewLabel$default.setPadding(0, dp(10), 0, dp(18));
        this.fileView = textViewLabel$default;
        if (textViewLabel$default == null) {
            Intrinsics.throwUninitializedPropertyAccessException("fileView");
            textViewLabel$default = null;
        }
        linearLayout.addView(textViewLabel$default);
        linearLayout.addView(section("2. Indica quante persone parlano"));
        Spinner spinner = new Spinner(mainActivity);
        spinner.setAdapter((SpinnerAdapter) new ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item, CollectionsKt.listOf((Object[]) new String[]{"Automatico", "2 persone (consigliato)", "3 persone", "4 persone", "5 persone", "6 persone"})));
        spinner.setSelection(1);
        spinner.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(36, 104, 89)));
        this.speakerSpinner = spinner;
        linearLayout.addView(spinner, new LinearLayout.LayoutParams(-1, dp(52)));
        TextView textViewLabel$default2 = label$default(this, "Se conosci il numero esatto, selezionalo: le etichette saranno più stabili.", 13.0f, Color.rgb(100, 105, 106), 0, 8, null);
        textViewLabel$default2.setPadding(0, dp(6), 0, dp(18));
        linearLayout.addView(textViewLabel$default2);
        linearLayout.addView(section("3. Avvia"));
        Button buttonPrimaryButton2 = primaryButton("Trascrivi ora", new Function0<Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity.createInterface.8
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                MainActivity.this.startTranscription();
            }
        });
        buttonPrimaryButton2.setEnabled(false);
        this.startButton = buttonPrimaryButton2;
        if (buttonPrimaryButton2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("startButton");
            buttonPrimaryButton2 = null;
        }
        linearLayout.addView(buttonPrimaryButton2);
        Button buttonSecondaryButton = secondaryButton("Annulla", new Function0<Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity.createInterface.10
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                MainActivity.this.cancelled.set(true);
                TextView textView = MainActivity.this.statusView;
                Button button = null;
                if (textView == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("statusView");
                    textView = null;
                }
                textView.setText("Interruzione in corso…");
                Button button2 = MainActivity.this.cancelButton;
                if (button2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("cancelButton");
                } else {
                    button = button2;
                }
                button.setEnabled(false);
            }
        });
        buttonSecondaryButton.setVisibility(8);
        this.cancelButton = buttonSecondaryButton;
        if (buttonSecondaryButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cancelButton");
            buttonSecondaryButton = null;
        }
        linearLayout.addView(buttonSecondaryButton);
        ProgressBar progressBar = new ProgressBar(mainActivity, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(36, 104, 89)));
        progressBar.setVisibility(8);
        this.progressBar = progressBar;
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, dp(12));
        layoutParams2.setMargins(0, dp(18), 0, dp(8));
        Unit unit = Unit.INSTANCE;
        linearLayout.addView(progressBar, layoutParams2);
        TextView textViewLabel4 = label("Pronto", 14.0f, Color.rgb(72, 80, 82), 1);
        textViewLabel4.setPadding(0, dp(5), 0, dp(16));
        this.statusView = textViewLabel4;
        if (textViewLabel4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
            textViewLabel4 = null;
        }
        linearLayout.addView(textViewLabel4);
        TextView textViewSection = section("Testo della conversazione");
        textViewSection.setPadding(0, dp(12), 0, dp(8));
        linearLayout.addView(textViewSection);
        LinearLayout linearLayout2 = new LinearLayout(mainActivity);
        linearLayout2.setPadding(dp(16), dp(16), dp(16), dp(16));
        linearLayout2.setBackground(rounded(-1, 15, Integer.valueOf(Color.rgb(220, 218, 209))));
        TextView textViewLabel$default3 = label$default(this, "Al termine compariranno qui le battute divise per persona.", 15.0f, Color.rgb(92, 96, 97), 0, 8, null);
        textViewLabel$default3.setTextIsSelectable(true);
        textViewLabel$default3.setLineSpacing(0.0f, 1.15f);
        this.resultView = textViewLabel$default3;
        if (textViewLabel$default3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textViewLabel$default3 = null;
        }
        linearLayout2.addView(textViewLabel$default3);
        linearLayout.addView(linearLayout2);
        LinearLayout linearLayout3 = new LinearLayout(mainActivity);
        linearLayout3.setOrientation(0);
        linearLayout3.setGravity(16);
        Button buttonSecondaryButton2 = secondaryButton("Copia testo", new Function0<Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity.createInterface.17
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                MainActivity.this.copyTranscript();
            }
        });
        buttonSecondaryButton2.setEnabled(false);
        this.copyButton = buttonSecondaryButton2;
        Button buttonSecondaryButton3 = secondaryButton("Salva TXT", new Function0<Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity.createInterface.19
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                MainActivity.this.openSavePicker();
            }
        });
        buttonSecondaryButton3.setEnabled(false);
        this.saveButton = buttonSecondaryButton3;
        Button button = this.copyButton;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button = null;
        }
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, dp(52), 1.0f);
        layoutParams3.setMargins(0, dp(14), dp(6), 0);
        Unit unit2 = Unit.INSTANCE;
        linearLayout3.addView(button, layoutParams3);
        Button button2 = this.saveButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            button2 = null;
        }
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(0, dp(52), 1.0f);
        layoutParams4.setMargins(dp(6), dp(14), 0, 0);
        Unit unit3 = Unit.INSTANCE;
        linearLayout3.addView(button2, layoutParams4);
        linearLayout.addView(linearLayout3);
        TextView textViewLabel$default4 = label$default(this, "La precisione dipende dalla registrazione. Voci sovrapposte, molto lontane o musica di fondo possono richiedere piccole correzioni.", 12.0f, Color.rgb(110, 113, 113), 0, 8, null);
        textViewLabel$default4.setPadding(0, dp(24), 0, 0);
        linearLayout.addView(textViewLabel$default4);
        ScrollView scrollView = new ScrollView(mainActivity);
        scrollView.addView(linearLayout);
        setContentView(scrollView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void openAudioPicker() {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("audio/*");
        intent.addFlags(65);
        startActivityForResult(intent, REQUEST_AUDIO);
    }

    private final void selectAudio(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(uri, 1);
        } catch (SecurityException unused) {
        }
        this.selectedUri = uri;
        this.selectedName = displayName(uri);
        TextView textView = this.fileView;
        TextView textView2 = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("fileView");
            textView = null;
        }
        textView.setText(this.selectedName);
        TextView textView3 = this.fileView;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("fileView");
            textView3 = null;
        }
        textView3.setTextColor(Color.rgb(36, 104, 89));
        Button button = this.startButton;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("startButton");
            button = null;
        }
        button.setEnabled(true);
        Button button2 = this.copyButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button2 = null;
        }
        button2.setEnabled(false);
        Button button3 = this.saveButton;
        if (button3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            button3 = null;
        }
        button3.setEnabled(false);
        this.transcript = "";
        TextView textView4 = this.resultView;
        if (textView4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textView4 = null;
        }
        textView4.setText("File pronto. Tocca “Trascrivi ora”.");
        TextView textView5 = this.statusView;
        if (textView5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
        } else {
            textView2 = textView5;
        }
        textView2.setText("Pronto");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startTranscription() {
        final Uri uri = this.selectedUri;
        if (uri == null || this.running) {
            return;
        }
        this.running = true;
        this.cancelled.set(false);
        this.transcript = "";
        Button button = this.chooseButton;
        Spinner spinner = null;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("chooseButton");
            button = null;
        }
        button.setEnabled(false);
        Button button2 = this.startButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("startButton");
            button2 = null;
        }
        button2.setEnabled(false);
        Spinner spinner2 = this.speakerSpinner;
        if (spinner2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("speakerSpinner");
            spinner2 = null;
        }
        spinner2.setEnabled(false);
        Button button3 = this.copyButton;
        if (button3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button3 = null;
        }
        button3.setEnabled(false);
        Button button4 = this.saveButton;
        if (button4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            button4 = null;
        }
        button4.setEnabled(false);
        Button button5 = this.cancelButton;
        if (button5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cancelButton");
            button5 = null;
        }
        button5.setVisibility(0);
        Button button6 = this.cancelButton;
        if (button6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cancelButton");
            button6 = null;
        }
        button6.setEnabled(true);
        ProgressBar progressBar = this.progressBar;
        if (progressBar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            progressBar = null;
        }
        progressBar.setVisibility(0);
        ProgressBar progressBar2 = this.progressBar;
        if (progressBar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            progressBar2 = null;
        }
        progressBar2.setProgress(1);
        TextView textView = this.statusView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
            textView = null;
        }
        textView.setText("Preparazione dell’audio…");
        TextView textView2 = this.resultView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textView2 = null;
        }
        textView2.setText("Elaborazione in corso. Tieni l’app aperta; una registrazione lunga può richiedere tempo.");
        getWindow().addFlags(128);
        int[] iArr = SPEAKER_COUNTS;
        Spinner spinner3 = this.speakerSpinner;
        if (spinner3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("speakerSpinner");
        } else {
            spinner = spinner3;
        }
        final int i = iArr[spinner.getSelectedItemPosition()];
        this.worker.execute(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.startTranscription$lambda$30(this.f$0, uri, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:35:0x00fd  */
    /* JADX WARN: Code duplicated, block: B:37:0x0102  */
    /* JADX WARN: Code duplicated, block: B:39:0x0107  */
    /* JADX WARN: Code duplicated, block: B:44:0x0127  */
    /* JADX WARN: Code duplicated, block: B:46:0x012c  */
    /* JADX WARN: Code duplicated, block: B:48:0x0131  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v18, types: [T, java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v18 */
    /* JADX WARN: Type inference failed for: r2v19 */
    /* JADX WARN: Type inference failed for: r2v6 */
    public static final void startTranscription$lambda$30(final MainActivity this$0, Uri uri, int i) {
        LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine;
        File fileCreateTempFile;
        Runnable runnable;
        LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine2 = "TXT creato in ";
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(uri, "$uri");
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        final Ref.IntRef intRef = new Ref.IntRef();
        ProgressiveTranscriptWriter progressiveTranscriptWriter = null;
        try {
            try {
                fileCreateTempFile = File.createTempFile("trascrivi-", ".pcm", this$0.getCacheDir());
                try {
                    DecodedAudio decodedAudioDecode = new AudioDecoder(this$0).decode(uri, fileCreateTempFile, this$0.cancelled, new Function1<Integer, Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$startTranscription$1$audio$1
                        {
                            super(1);
                        }

                        @Override // kotlin.jvm.functions.Function1
                        public /* bridge */ /* synthetic */ Unit invoke(Integer num) {
                            invoke(num.intValue());
                            return Unit.INSTANCE;
                        }

                        public final void invoke(int i2) {
                            this.this$0.updateProgress(((i2 * 18) / 100) + 2, "Conversione audio… " + i2 + '%');
                        }
                    });
                    if (this$0.cancelled.get()) {
                        throw new CancellationException();
                    }
                    this$0.rememberCurrentAudio();
                    final ProgressiveTranscriptWriter progressiveTranscriptWriterCreate = ProgressiveTranscriptWriter.INSTANCE.create(this$0, this$0.selectedName);
                    try {
                        objectRef.element = progressiveTranscriptWriterCreate.getDisplayPath();
                        progressiveTranscriptWriterCreate.append(this$0.transcriptHeader(decodedAudioDecode.getDurationSeconds()));
                        this$0.updateProgress(20, "TXT creato in " + ((String) objectRef.element));
                        AssetManager assets = this$0.getAssets();
                        Intrinsics.checkNotNullExpressionValue(assets, "getAssets(...)");
                        lowMemoryTranscriptionEngine = new LowMemoryTranscriptionEngine(assets);
                        try {
                            final List<TranscriptLine> listTranscribe = lowMemoryTranscriptionEngine.transcribe(decodedAudioDecode, i, this$0.cancelled, new MainActivity$startTranscription$1$lines$1(this$0), new Function1<TranscriptLine, Unit>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$startTranscription$1$lines$2
                                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                {
                                    super(1);
                                }

                                @Override // kotlin.jvm.functions.Function1
                                public /* bridge */ /* synthetic */ Unit invoke(TranscriptLine transcriptLine) {
                                    invoke2(transcriptLine);
                                    return Unit.INSTANCE;
                                }

                                /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
                                public final void invoke2(TranscriptLine line) {
                                    Intrinsics.checkNotNullParameter(line, "line");
                                    progressiveTranscriptWriterCreate.append(this$0.formatTranscriptLine(line));
                                    intRef.element++;
                                }
                            });
                            if (listTranscribe.isEmpty()) {
                                progressiveTranscriptWriterCreate.append("Nessun parlato chiaro rilevato.\n\n");
                            }
                            progressiveTranscriptWriterCreate.append("--- Trascrizione completata ---\n");
                            progressiveTranscriptWriterCreate.close();
                            try {
                                this$0.transcript = this$0.readRecoveryTranscript();
                                this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$25(this.f$0, listTranscribe, objectRef, intRef);
                                    }
                                });
                                lowMemoryTranscriptionEngine.release();
                                if (fileCreateTempFile != null) {
                                    fileCreateTempFile.delete();
                                }
                                runnable = new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                                    }
                                };
                            } catch (CancellationException unused) {
                                this$0.safeAppend(progressiveTranscriptWriter, "\n--- Trascrizione interrotta dall’utente ---\n");
                                this$0.transcript = this$0.readRecoveryTranscript();
                                this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$26(this.f$0, objectRef);
                                    }
                                });
                                if (progressiveTranscriptWriter != null) {
                                    progressiveTranscriptWriter.close();
                                }
                                if (lowMemoryTranscriptionEngine != null) {
                                    lowMemoryTranscriptionEngine.release();
                                }
                                if (fileCreateTempFile != null) {
                                    fileCreateTempFile.delete();
                                }
                                runnable = new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                                    }
                                };
                                lowMemoryTranscriptionEngine2 = lowMemoryTranscriptionEngine;
                            } catch (Throwable th) {
                                th = th;
                                this$0.safeAppend(progressiveTranscriptWriter, "\n--- Trascrizione interrotta prima della fine ---\n");
                                this$0.transcript = this$0.readRecoveryTranscript();
                                this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda3
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$28(this.f$0, th, objectRef);
                                    }
                                });
                                if (progressiveTranscriptWriter != null) {
                                    progressiveTranscriptWriter.close();
                                }
                                if (lowMemoryTranscriptionEngine != null) {
                                    lowMemoryTranscriptionEngine.release();
                                }
                                if (fileCreateTempFile != null) {
                                    fileCreateTempFile.delete();
                                }
                                runnable = new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                                    }
                                };
                                lowMemoryTranscriptionEngine2 = lowMemoryTranscriptionEngine;
                            }
                        } catch (CancellationException unused2) {
                            progressiveTranscriptWriter = progressiveTranscriptWriterCreate;
                            this$0.safeAppend(progressiveTranscriptWriter, "\n--- Trascrizione interrotta dall’utente ---\n");
                            this$0.transcript = this$0.readRecoveryTranscript();
                            this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MainActivity.startTranscription$lambda$30$lambda$26(this.f$0, objectRef);
                                }
                            });
                            if (progressiveTranscriptWriter != null) {
                                progressiveTranscriptWriter.close();
                            }
                            if (lowMemoryTranscriptionEngine != null) {
                                lowMemoryTranscriptionEngine.release();
                            }
                            if (fileCreateTempFile != null) {
                                fileCreateTempFile.delete();
                            }
                            runnable = new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                                }
                            };
                            lowMemoryTranscriptionEngine2 = lowMemoryTranscriptionEngine;
                            this$0.runOnUiThread(runnable);
                        } catch (Throwable th2) {
                            th = th2;
                            progressiveTranscriptWriter = progressiveTranscriptWriterCreate;
                            this$0.safeAppend(progressiveTranscriptWriter, "\n--- Trascrizione interrotta prima della fine ---\n");
                            this$0.transcript = this$0.readRecoveryTranscript();
                            this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda3
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MainActivity.startTranscription$lambda$30$lambda$28(this.f$0, th, objectRef);
                                }
                            });
                            if (progressiveTranscriptWriter != null) {
                                progressiveTranscriptWriter.close();
                            }
                            if (lowMemoryTranscriptionEngine != null) {
                                lowMemoryTranscriptionEngine.release();
                            }
                            if (fileCreateTempFile != null) {
                                fileCreateTempFile.delete();
                            }
                            runnable = new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                                }
                            };
                            lowMemoryTranscriptionEngine2 = lowMemoryTranscriptionEngine;
                            this$0.runOnUiThread(runnable);
                        }
                    } catch (CancellationException unused3) {
                        lowMemoryTranscriptionEngine = null;
                    } catch (Throwable th3) {
                        th = th3;
                        lowMemoryTranscriptionEngine = null;
                    }
                    this$0.runOnUiThread(runnable);
                } catch (CancellationException unused4) {
                    lowMemoryTranscriptionEngine = null;
                } catch (Throwable th4) {
                    th = th4;
                    lowMemoryTranscriptionEngine = null;
                }
            } catch (Throwable th5) {
                if (progressiveTranscriptWriter != null) {
                    progressiveTranscriptWriter.close();
                }
                if (lowMemoryTranscriptionEngine2 != 0) {
                    lowMemoryTranscriptionEngine2.release();
                }
                if (fileCreateTempFile != null) {
                    fileCreateTempFile.delete();
                }
                this$0.runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.startTranscription$lambda$30$lambda$29(this.f$0);
                    }
                });
                throw th5;
            }
        } catch (CancellationException unused5) {
            lowMemoryTranscriptionEngine = null;
            fileCreateTempFile = null;
        } catch (Throwable th6) {
            th = th6;
            lowMemoryTranscriptionEngine = null;
            fileCreateTempFile = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void startTranscription$lambda$30$lambda$25(MainActivity this$0, List lines, Ref.ObjectRef savedPath, Ref.IntRef lineCount) {
        String str;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(lines, "$lines");
        Intrinsics.checkNotNullParameter(savedPath, "$savedPath");
        Intrinsics.checkNotNullParameter(lineCount, "$lineCount");
        ProgressBar progressBar = this$0.progressBar;
        Button button = null;
        if (progressBar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            progressBar = null;
        }
        progressBar.setProgress(100);
        TextView textView = this$0.statusView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
            textView = null;
        }
        if (lines.isEmpty()) {
            str = "Completato: nessun parlato chiaro. TXT salvato in " + ((String) savedPath.element);
        } else {
            str = "Completato: " + lineCount.element + " interventi. TXT salvato in " + ((String) savedPath.element);
        }
        textView.setText(str);
        TextView textView2 = this$0.resultView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textView2 = null;
        }
        textView2.setText(this$0.preview(this$0.transcript));
        Button button2 = this$0.copyButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button2 = null;
        }
        button2.setEnabled(true);
        Button button3 = this$0.saveButton;
        if (button3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
        } else {
            button = button3;
        }
        button.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void startTranscription$lambda$30$lambda$26(MainActivity this$0, Ref.ObjectRef savedPath) {
        String str;
        String strPreview;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(savedPath, "$savedPath");
        TextView textView = this$0.statusView;
        ProgressBar progressBar = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
            textView = null;
        }
        if (savedPath.element != 0) {
            str = "Interrotta. Il TXT parziale è stato mantenuto in " + ((String) savedPath.element);
        }
        textView.setText(str);
        TextView textView2 = this$0.resultView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textView2 = null;
        }
        if (!StringsKt.isBlank(this$0.transcript)) {
            strPreview = this$0.preview(this$0.transcript);
        }
        textView2.setText(strPreview);
        Button button = this$0.copyButton;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button = null;
        }
        button.setEnabled(!StringsKt.isBlank(this$0.transcript));
        Button button2 = this$0.saveButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            button2 = null;
        }
        button2.setEnabled(!StringsKt.isBlank(this$0.transcript));
        ProgressBar progressBar2 = this$0.progressBar;
        if (progressBar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
        } else {
            progressBar = progressBar2;
        }
        progressBar.setProgress(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final void startTranscription$lambda$30$lambda$28(MainActivity this$0, Throwable error, Ref.ObjectRef savedPath) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(error, "$error");
        Intrinsics.checkNotNullParameter(savedPath, "$savedPath");
        TextView textView = this$0.statusView;
        ProgressBar progressBar = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
            textView = null;
        }
        textView.setText("Trascrizione non completata.");
        TextView textView2 = this$0.resultView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("resultView");
            textView2 = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this$0.friendlyError(error));
        if (savedPath.element != 0) {
            sb.append("\n\nIl testo già prodotto è stato mantenuto in ");
            sb.append((String) savedPath.element);
            sb.append('.');
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
        textView2.setText(string);
        Button button = this$0.copyButton;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("copyButton");
            button = null;
        }
        button.setEnabled(!StringsKt.isBlank(this$0.transcript));
        Button button2 = this$0.saveButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            button2 = null;
        }
        button2.setEnabled(!StringsKt.isBlank(this$0.transcript));
        ProgressBar progressBar2 = this$0.progressBar;
        if (progressBar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
        } else {
            progressBar = progressBar2;
        }
        progressBar.setProgress(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startTranscription$lambda$30$lambda$29(MainActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.endRunningState();
    }

    private final void rememberCurrentAudio() {
        getSharedPreferences(PREFS, 0).edit().putString(PREF_LAST_AUDIO, this.selectedName).apply();
    }

    private final void restoreLastTranscript() {
        Object objM6constructorimpl;
        String str;
        File file = new File(getFilesDir(), ProgressiveTranscriptWriterKt.RECOVERY_FILE_NAME);
        if (!file.isFile() || file.length() == 0) {
            return;
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            MainActivity mainActivity = this;
            objM6constructorimpl = Result.m6constructorimpl(FilesKt.readText(file, Charsets.UTF_8));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM6constructorimpl = Result.m6constructorimpl(ResultKt.createFailure(th));
        }
        Button button = null;
        if (Result.m12isFailureimpl(objM6constructorimpl)) {
            objM6constructorimpl = null;
        }
        String str2 = (String) objM6constructorimpl;
        if (str2 != null) {
            this.transcript = str2;
            String string = getSharedPreferences(PREFS, 0).getString(PREF_LAST_AUDIO, "registrazione");
            if (string == null) {
                string = "";
            }
            String str3 = string;
            this.selectedName = StringsKt.isBlank(str3) ? "registrazione" : str3;
            TextView textView = this.resultView;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("resultView");
                textView = null;
            }
            textView.setText(preview(str2));
            TextView textView2 = this.statusView;
            if (textView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusView");
                textView2 = null;
            }
            if (StringsKt.contains$default((CharSequence) str2, (CharSequence) ProgressiveTranscriptWriterKt.TRANSCRIPT_COMPLETE_MARKER, false, 2, (Object) null)) {
            }
            textView2.setText(str);
            Button button2 = this.copyButton;
            if (button2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("copyButton");
                button2 = null;
            }
            button2.setEnabled(true);
            Button button3 = this.saveButton;
            if (button3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            } else {
                button = button3;
            }
            button.setEnabled(true);
        }
    }

    private final String readRecoveryTranscript() {
        Object objM6constructorimpl;
        File file = new File(getFilesDir(), ProgressiveTranscriptWriterKt.RECOVERY_FILE_NAME);
        if (!file.isFile() || file.length() == 0) {
            return "";
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            MainActivity mainActivity = this;
            objM6constructorimpl = Result.m6constructorimpl(FilesKt.readText(file, Charsets.UTF_8));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM6constructorimpl = Result.m6constructorimpl(ResultKt.createFailure(th));
        }
        return (String) (Result.m12isFailureimpl(objM6constructorimpl) ? "" : objM6constructorimpl);
    }

    private final void safeAppend(ProgressiveTranscriptWriter writer, String text) {
        if (writer != null) {
            try {
                Result.Companion companion = Result.INSTANCE;
                MainActivity mainActivity = this;
                writer.append(text);
                Result.m6constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                Result.Companion companion2 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th));
            }
        }
    }

    private final String transcriptHeader(double duration) {
        StringBuilder sb = new StringBuilder();
        sb.append("TRASCRIZIONE OFFLINE");
        Intrinsics.checkNotNullExpressionValue(sb, "append(value)");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        sb.append("File: " + this.selectedName);
        Intrinsics.checkNotNullExpressionValue(sb, "append(value)");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        sb.append("Durata: " + time(duration));
        Intrinsics.checkNotNullExpressionValue(sb, "append(value)");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        sb.append("Generata: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY).format(new Date()));
        Intrinsics.checkNotNullExpressionValue(sb, "append(value)");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String formatTranscriptLine(TranscriptLine line) {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + time(line.getStartSeconds()) + "] Persona " + line.getSpeaker() + ": " + line.getText());
        Intrinsics.checkNotNullExpressionValue(sb, "append(value)");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        sb.append('\n');
        Intrinsics.checkNotNullExpressionValue(sb, "append('\\n')");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "StringBuilder().apply(builderAction).toString()");
        return string;
    }

    private final String preview(String text) {
        if (text.length() <= PREVIEW_LIMIT) {
            return text;
        }
        return StringsKt.take(text, PREVIEW_LIMIT) + "\n\n[Anteprima abbreviata. Il file TXT salvato contiene la trascrizione completa.]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateProgress(final int value, final String status) {
        runOnUiThread(new Runnable() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.updateProgress$lambda$38(this.f$0, value, status);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void updateProgress$lambda$38(MainActivity this$0, int i, String status) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(status, "$status");
        ProgressBar progressBar = this$0.progressBar;
        TextView textView = null;
        if (progressBar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            progressBar = null;
        }
        progressBar.setProgress(RangesKt.coerceIn(i, 0, 100));
        TextView textView2 = this$0.statusView;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("statusView");
        } else {
            textView = textView2;
        }
        textView.setText(status);
    }

    private final void endRunningState() {
        this.running = false;
        Button button = this.chooseButton;
        ProgressBar progressBar = null;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("chooseButton");
            button = null;
        }
        button.setEnabled(true);
        Button button2 = this.startButton;
        if (button2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("startButton");
            button2 = null;
        }
        button2.setEnabled(this.selectedUri != null);
        Spinner spinner = this.speakerSpinner;
        if (spinner == null) {
            Intrinsics.throwUninitializedPropertyAccessException("speakerSpinner");
            spinner = null;
        }
        spinner.setEnabled(true);
        Button button3 = this.cancelButton;
        if (button3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cancelButton");
            button3 = null;
        }
        button3.setVisibility(8);
        ProgressBar progressBar2 = this.progressBar;
        if (progressBar2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            progressBar2 = null;
        }
        if (progressBar2.getProgress() != 100) {
            ProgressBar progressBar3 = this.progressBar;
            if (progressBar3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("progressBar");
            } else {
                progressBar = progressBar3;
            }
            progressBar.setVisibility(8);
        }
        getWindow().clearFlags(128);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void copyTranscript() {
        if (StringsKt.isBlank(this.transcript)) {
            return;
        }
        Object systemService = getSystemService("clipboard");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.content.ClipboardManager");
        ((ClipboardManager) systemService).setPrimaryClip(ClipData.newPlainText("Trascrizione", this.transcript));
        Toast.makeText(this, "Testo copiato", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void openSavePicker() {
        if (StringsKt.isBlank(this.transcript)) {
            return;
        }
        String str = this.selectedName;
        String strSubstringBeforeLast = StringsKt.substringBeforeLast(str, '.', str);
        if (StringsKt.isBlank(strSubstringBeforeLast)) {
            strSubstringBeforeLast = "registrazione";
        }
        Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TITLE", strSubstringBeforeLast + "_trascrizione.txt");
        startActivityForResult(intent, REQUEST_SAVE);
    }

    private final void saveTranscript(Uri uri) {
        try {
            OutputStream outputStreamOpenOutputStream = getContentResolver().openOutputStream(uri, "wt");
            Unit unit = null;
            if (outputStreamOpenOutputStream != null) {
                Writer outputStreamWriter = new OutputStreamWriter(outputStreamOpenOutputStream, Charsets.UTF_8);
                BufferedWriter bufferedWriter = outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, ConstantsKt.DEFAULT_BUFFER_SIZE);
                try {
                    bufferedWriter.write(this.transcript);
                    Unit unit2 = Unit.INSTANCE;
                    CloseableKt.closeFinally(bufferedWriter, null);
                    unit = Unit.INSTANCE;
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(bufferedWriter, th);
                        throw th2;
                    }
                }
            }
            if (unit == null) {
                throw new IllegalStateException("Destinazione non disponibile".toString());
            }
            Toast.makeText(this, "File TXT salvato", 1).show();
        } catch (Throwable th3) {
            Toast.makeText(this, "Errore nel salvataggio: " + th3.getMessage(), 1).show();
        }
    }

    private final String displayName(Uri uri) throws IOException {
        int columnIndex;
        Cursor cursorQuery = getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
        if (cursorQuery != null) {
            Cursor cursor = cursorQuery;
            try {
                Cursor cursor2 = cursor;
                if (cursor2.moveToFirst() && (columnIndex = cursor2.getColumnIndex("_display_name")) >= 0) {
                    String string = cursor2.getString(columnIndex);
                    Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                    CloseableKt.closeFinally(cursor, null);
                    return string;
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(cursor, null);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(cursor, th);
                    throw th2;
                }
            }
        }
        String lastPathSegment = uri.getLastPathSegment();
        return lastPathSegment == null ? "registrazione" : lastPathSegment;
    }

    private final String friendlyError(Throwable error) {
        String message = error.getMessage();
        if (message == null) {
            message = "";
        }
        String str = message;
        if (StringsKt.contains((CharSequence) str, (CharSequence) "decoder", true) || StringsKt.contains((CharSequence) str, (CharSequence) "codec", true) || StringsKt.contains((CharSequence) str, (CharSequence) "formato", true)) {
            return "Il formato non è supportato dal telefono. Prova a esportare il file come M4A, MP3 o WAV.";
        }
        if (StringsKt.contains((CharSequence) str, (CharSequence) "memory", true)) {
            return "La registrazione è troppo grande per la memoria disponibile. Dividila in due parti e riprova.";
        }
        if (!(!StringsKt.isBlank(str))) {
            return "Errore inatteso. Prova con una registrazione più breve.";
        }
        return "Dettaglio: " + message;
    }

    private final String time(double seconds) {
        long jCoerceAtLeast = RangesKt.coerceAtLeast((long) seconds, 0L);
        long j = 3600;
        long j2 = jCoerceAtLeast / j;
        long j3 = 60;
        long j4 = (jCoerceAtLeast % j) / j3;
        long j5 = jCoerceAtLeast % j3;
        if (j2 > 0) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str = String.format(Locale.ITALY, "%02d:%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(j2), Long.valueOf(j4), Long.valueOf(j5)}, 3));
            Intrinsics.checkNotNullExpressionValue(str, "format(locale, format, *args)");
            return str;
        }
        StringCompanionObject stringCompanionObject2 = StringCompanionObject.INSTANCE;
        String str2 = String.format(Locale.ITALY, "%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(j4), Long.valueOf(j5)}, 2));
        Intrinsics.checkNotNullExpressionValue(str2, "format(locale, format, *args)");
        return str2;
    }

    private final TextView section(String text) {
        TextView textViewLabel = label(text, 17.0f, Color.rgb(28, 45, 54), 1);
        textViewLabel.setPadding(0, dp(5), 0, dp(9));
        return textViewLabel;
    }

    private final Button primaryButton(String text, final Function0<Unit> action) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(16.0f);
        button.setAllCaps(false);
        button.setTextColor(-1);
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(36, 104, 89)));
        button.setOnClickListener(new View.OnClickListener() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.primaryButton$lambda$45$lambda$44(action, view);
            }
        });
        button.setMinHeight(dp(54));
        return button;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void primaryButton$lambda$45$lambda$44(Function0 action, View view) {
        Intrinsics.checkNotNullParameter(action, "$action");
        action.invoke();
    }

    private final Button secondaryButton(String text, final Function0<Unit> action) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(14.0f);
        button.setAllCaps(false);
        button.setTextColor(Color.rgb(28, 45, 54));
        button.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(225, 228, 222)));
        button.setOnClickListener(new View.OnClickListener() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.MainActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.secondaryButton$lambda$47$lambda$46(action, view);
            }
        });
        return button;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void secondaryButton$lambda$47$lambda$46(Function0 action, View view) {
        Intrinsics.checkNotNullParameter(action, "$action");
        action.invoke();
    }

    private final TextView label(String text, float size, int color, int style) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(color);
        textView.setTypeface(textView.getTypeface(), style);
        return textView;
    }

    static /* synthetic */ TextView label$default(MainActivity mainActivity, String str, float f, int i, int i2, int i3, Object obj) {
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        return mainActivity.label(str, f, i, i2);
    }

    private final GradientDrawable rounded(int fill, int radius, Integer stroke) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(fill);
        gradientDrawable.setCornerRadius(dp(radius));
        if (stroke != null) {
            gradientDrawable.setStroke(dp(1), stroke.intValue());
        }
        return gradientDrawable;
    }

    static /* synthetic */ GradientDrawable rounded$default(MainActivity mainActivity, int i, int i2, Integer num, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            num = null;
        }
        return mainActivity.rounded(i, i2, num);
    }

    private final int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
