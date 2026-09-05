package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: ProgressiveTranscriptWriter.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B9\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bJ\b\u0010\u0014\u001a\u00020\u0012H\u0016R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/ProgressiveTranscriptWriter;", "Ljava/io/Closeable;", "internalOutput", "Ljava/io/FileOutputStream;", "internalWriter", "Ljava/io/BufferedWriter;", "downloadOutput", "downloadWriter", "downloadDescriptor", "Landroid/os/ParcelFileDescriptor;", "displayPath", "", "(Ljava/io/FileOutputStream;Ljava/io/BufferedWriter;Ljava/io/FileOutputStream;Ljava/io/BufferedWriter;Landroid/os/ParcelFileDescriptor;Ljava/lang/String;)V", "closed", "", "getDisplayPath", "()Ljava/lang/String;", "append", "", "text", "close", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final class ProgressiveTranscriptWriter implements Closeable {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private boolean closed;
    private final String displayPath;
    private final ParcelFileDescriptor downloadDescriptor;
    private final FileOutputStream downloadOutput;
    private final BufferedWriter downloadWriter;
    private final FileOutputStream internalOutput;
    private final BufferedWriter internalWriter;

    public /* synthetic */ ProgressiveTranscriptWriter(FileOutputStream fileOutputStream, BufferedWriter bufferedWriter, FileOutputStream fileOutputStream2, BufferedWriter bufferedWriter2, ParcelFileDescriptor parcelFileDescriptor, String str, DefaultConstructorMarker defaultConstructorMarker) {
        this(fileOutputStream, bufferedWriter, fileOutputStream2, bufferedWriter2, parcelFileDescriptor, str);
    }

    private ProgressiveTranscriptWriter(FileOutputStream fileOutputStream, BufferedWriter bufferedWriter, FileOutputStream fileOutputStream2, BufferedWriter bufferedWriter2, ParcelFileDescriptor parcelFileDescriptor, String str) {
        this.internalOutput = fileOutputStream;
        this.internalWriter = bufferedWriter;
        this.downloadOutput = fileOutputStream2;
        this.downloadWriter = bufferedWriter2;
        this.downloadDescriptor = parcelFileDescriptor;
        this.displayPath = str;
    }

    public final String getDisplayPath() {
        return this.displayPath;
    }

    public final synchronized void append(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        if (!(!this.closed)) {
            throw new IllegalStateException("Il file TXT è già stato chiuso".toString());
        }
        this.internalWriter.write(text);
        this.internalWriter.flush();
        try {
            Result.Companion companion = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter = this;
            this.internalOutput.getFD().sync();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th));
        }
        this.downloadWriter.write(text);
        this.downloadWriter.flush();
        try {
            Result.Companion companion3 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter2 = this;
            this.downloadOutput.getFD().sync();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th2) {
            Result.Companion companion4 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th2));
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() {
        Unit unit;
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            Result.Companion companion = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter = this;
            this.internalWriter.flush();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th));
        }
        try {
            Result.Companion companion3 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter2 = this;
            this.internalOutput.getFD().sync();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th2) {
            Result.Companion companion4 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th2));
        }
        try {
            Result.Companion companion5 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter3 = this;
            this.downloadWriter.flush();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th3) {
            Result.Companion companion6 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th3));
        }
        try {
            Result.Companion companion7 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter4 = this;
            this.downloadOutput.getFD().sync();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th4) {
            Result.Companion companion8 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th4));
        }
        try {
            Result.Companion companion9 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter5 = this;
            this.internalWriter.close();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th5) {
            Result.Companion companion10 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th5));
        }
        try {
            Result.Companion companion11 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter6 = this;
            this.downloadWriter.close();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th6) {
            Result.Companion companion12 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th6));
        }
        try {
            Result.Companion companion13 = Result.INSTANCE;
            ProgressiveTranscriptWriter progressiveTranscriptWriter7 = this;
            ParcelFileDescriptor parcelFileDescriptor = this.downloadDescriptor;
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
                unit = Unit.INSTANCE;
            } else {
                unit = null;
            }
            Result.m6constructorimpl(unit);
        } catch (Throwable th7) {
            Result.Companion companion14 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th7));
        }
    }

    /* JADX INFO: compiled from: ProgressiveTranscriptWriter.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b¨\u0006\t"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/ProgressiveTranscriptWriter$Companion;", "", "()V", "create", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/ProgressiveTranscriptWriter;", "context", "Landroid/content/Context;", "audioName", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code duplicated, block: B:47:0x0177 A[Catch: all -> 0x0182, TryCatch #1 {all -> 0x0182, blocks: (B:45:0x0170, B:47:0x0177, B:49:0x017e), top: B:81:0x0170 }] */
        /* JADX WARN: Code duplicated, block: B:48:0x017d  */
        /* JADX WARN: Code duplicated, block: B:55:0x0193 A[Catch: all -> 0x019e, TryCatch #9 {all -> 0x019e, blocks: (B:53:0x018c, B:55:0x0193, B:57:0x019a), top: B:96:0x018c }] */
        /* JADX WARN: Code duplicated, block: B:56:0x0199  */
        /* JADX WARN: Code duplicated, block: B:63:0x01af A[Catch: all -> 0x01ba, TryCatch #4 {all -> 0x01ba, blocks: (B:61:0x01a8, B:63:0x01af, B:65:0x01b6), top: B:87:0x01a8 }] */
        /* JADX WARN: Code duplicated, block: B:64:0x01b5  */
        /* JADX WARN: Code duplicated, block: B:92:0x01c6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v25 */
        /* JADX WARN: Type inference failed for: r0v31 */
        /* JADX WARN: Type inference failed for: r15v0 */
        /* JADX WARN: Type inference failed for: r15v1, types: [java.io.BufferedWriter] */
        /* JADX WARN: Type inference failed for: r15v2 */
        /* JADX WARN: Type inference failed for: r15v6 */
        /* JADX WARN: Type inference failed for: r15v8 */
        /* JADX WARN: Type inference failed for: r8v12 */
        /* JADX WARN: Type inference failed for: r8v21 */
        /* JADX WARN: Type inference failed for: r8v22 */
        /* JADX WARN: Type inference failed for: r8v23 */
        /* JADX WARN: Type inference failed for: r8v3, types: [java.io.FileOutputStream] */
        public final ProgressiveTranscriptWriter create(Context context, String audioName) throws Throwable {
            Throwable th;
            Uri uriInsert;
            ParcelFileDescriptor parcelFileDescriptorOpenFileDescriptor;
            ParcelFileDescriptor parcelFileDescriptor;
            ?? r15;
            ?? r8;
            Unit unit;
            Unit unit2;
            Unit unit3;
            String str;
            FileOutputStream fileOutputStream;
            ParcelFileDescriptor parcelFileDescriptor2;
            BufferedWriter bufferedWriter;
            BufferedWriter bufferedWriter2;
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(audioName, "audioName");
            FileOutputStream fileOutputStream2 = new FileOutputStream(new File(context.getFilesDir(), ProgressiveTranscriptWriterKt.RECOVERY_FILE_NAME), false);
            BufferedWriter bufferedWriter3 = new BufferedWriter(new OutputStreamWriter(fileOutputStream2, Charsets.UTF_8), 32768);
            try {
                String strSubstringBeforeLast = StringsKt.substringBeforeLast(audioName, '.', audioName);
                if (StringsKt.isBlank(strSubstringBeforeLast)) {
                    strSubstringBeforeLast = "registrazione";
                }
                String str2 = StringsKt.take(new Regex("[^\\p{L}\\p{N}_-]+").replace(strSubstringBeforeLast, "_"), 60) + "_trascrizione_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date()) + ".txt";
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", str2);
                    contentValues.put("mime_type", "text/plain");
                    contentValues.put("relative_path", Environment.DIRECTORY_DOWNLOADS + "/Trascrivi Offline");
                    uriInsert = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    if (uriInsert == null) {
                        throw new IllegalStateException("Impossibile creare il TXT nella cartella Download".toString());
                    }
                    try {
                        parcelFileDescriptorOpenFileDescriptor = context.getContentResolver().openFileDescriptor(uriInsert, "w");
                        if (parcelFileDescriptorOpenFileDescriptor == null) {
                            throw new IllegalStateException("Impossibile aprire il TXT nella cartella Download".toString());
                        }
                        try {
                            FileOutputStream fileOutputStream3 = new FileOutputStream(parcelFileDescriptorOpenFileDescriptor.getFileDescriptor());
                            try {
                                str = "Download/Trascrivi Offline/" + str2;
                                uriInsert = uriInsert;
                                parcelFileDescriptor2 = parcelFileDescriptorOpenFileDescriptor;
                                fileOutputStream = fileOutputStream3;
                                try {
                                    bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charsets.UTF_8), 32768);
                                    try {
                                        return new ProgressiveTranscriptWriter(fileOutputStream2, bufferedWriter3, fileOutputStream, bufferedWriter2, parcelFileDescriptor2, str, null);
                                    } catch (Throwable th2) {
                                        th = th2;
                                        bufferedWriter = bufferedWriter2;
                                        parcelFileDescriptorOpenFileDescriptor = parcelFileDescriptor2;
                                        r8 = fileOutputStream;
                                        th = th;
                                        r15 = bufferedWriter;
                                        Result.Companion companion = Result.INSTANCE;
                                        if (r15 != 0) {
                                            r15.close();
                                            unit3 = Unit.INSTANCE;
                                        } else {
                                            unit3 = null;
                                        }
                                        Result.m6constructorimpl(unit3);
                                        Result.Companion companion2 = Result.INSTANCE;
                                        if (r8 != 0) {
                                            r8.close();
                                            unit2 = Unit.INSTANCE;
                                        } else {
                                            unit2 = null;
                                        }
                                        Result.m6constructorimpl(unit2);
                                        Result.Companion companion3 = Result.INSTANCE;
                                        ParcelFileDescriptor parcelFileDescriptor3 = parcelFileDescriptorOpenFileDescriptor;
                                        if (parcelFileDescriptorOpenFileDescriptor != null) {
                                            parcelFileDescriptorOpenFileDescriptor.close();
                                            unit = Unit.INSTANCE;
                                        } else {
                                            unit = null;
                                        }
                                        Result.m6constructorimpl(unit);
                                        if (uriInsert != null) {
                                            try {
                                                Result.Companion companion4 = Result.INSTANCE;
                                                Uri uri = uriInsert;
                                                Result.m6constructorimpl(Integer.valueOf(context.getContentResolver().delete(uriInsert, null, null)));
                                            } catch (Throwable th3) {
                                                Result.Companion companion5 = Result.INSTANCE;
                                                Result.m6constructorimpl(ResultKt.createFailure(th3));
                                            }
                                        }
                                        Result.Companion companion6 = Result.INSTANCE;
                                        bufferedWriter3.close();
                                        Result.m6constructorimpl(Unit.INSTANCE);
                                        throw th;
                                    }
                                } catch (Throwable th4) {
                                    th = th4;
                                    bufferedWriter = null;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                r15 = 0;
                                r8 = fileOutputStream3;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            parcelFileDescriptor = null;
                            r15 = parcelFileDescriptor;
                            r8 = parcelFileDescriptor;
                        }
                    } catch (Throwable th7) {
                        th = th7;
                        parcelFileDescriptorOpenFileDescriptor = null;
                        parcelFileDescriptor = parcelFileDescriptorOpenFileDescriptor;
                    }
                    r15 = parcelFileDescriptor;
                    r8 = parcelFileDescriptor;
                } else {
                    File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    if (externalFilesDir == null) {
                        externalFilesDir = context.getFilesDir();
                    }
                    File file = new File(externalFilesDir, "Trascrivi Offline");
                    file.mkdirs();
                    File file2 = new File(file, str2);
                    FileOutputStream fileOutputStream4 = new FileOutputStream(file2, false);
                    try {
                        String absolutePath = file2.getAbsolutePath();
                        Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                        str = absolutePath;
                        fileOutputStream = fileOutputStream4;
                        uriInsert = null;
                        parcelFileDescriptor2 = null;
                        bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charsets.UTF_8), 32768);
                        return new ProgressiveTranscriptWriter(fileOutputStream2, bufferedWriter3, fileOutputStream, bufferedWriter2, parcelFileDescriptor2, str, null);
                    } catch (Throwable th8) {
                        th = th8;
                        uriInsert = null;
                        parcelFileDescriptorOpenFileDescriptor = null;
                        r15 = 0;
                        r8 = fileOutputStream4;
                    }
                }
            } catch (Throwable th9) {
                th = th9;
                uriInsert = null;
                parcelFileDescriptorOpenFileDescriptor = null;
            }
            try {
                Result.Companion companion7 = Result.INSTANCE;
                if (r15 != 0) {
                    r15.close();
                    unit3 = Unit.INSTANCE;
                } else {
                    unit3 = null;
                }
                Result.m6constructorimpl(unit3);
            } catch (Throwable th10) {
                Result.Companion companion8 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th10));
            }
            try {
                Result.Companion companion9 = Result.INSTANCE;
                if (r8 != 0) {
                    r8.close();
                    unit2 = Unit.INSTANCE;
                } else {
                    unit2 = null;
                }
                Result.m6constructorimpl(unit2);
            } catch (Throwable th11) {
                Result.Companion companion10 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th11));
            }
            try {
                Result.Companion companion11 = Result.INSTANCE;
                ParcelFileDescriptor parcelFileDescriptor4 = parcelFileDescriptorOpenFileDescriptor;
                if (parcelFileDescriptorOpenFileDescriptor != null) {
                    parcelFileDescriptorOpenFileDescriptor.close();
                    unit = Unit.INSTANCE;
                } else {
                    unit = null;
                }
                Result.m6constructorimpl(unit);
            } catch (Throwable th12) {
                Result.Companion companion12 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th12));
            }
            if (uriInsert != null) {
                Result.Companion companion13 = Result.INSTANCE;
                Uri uri2 = uriInsert;
                Result.m6constructorimpl(Integer.valueOf(context.getContentResolver().delete(uriInsert, null, null)));
            }
            try {
                Result.Companion companion14 = Result.INSTANCE;
                bufferedWriter3.close();
                Result.m6constructorimpl(Unit.INSTANCE);
                throw th;
            } catch (Throwable th13) {
                Result.Companion companion15 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th13));
                throw th;
            }
        }
    }
}
