package com.k2fsa.sherpa.onnx.speaker.diarization;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import kotlin.KotlinVersion;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: AudioDecoder.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0013J\b\u0010\u0015\u001a\u00020\u0013H\u0002J\u000e\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0018R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0019"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/Pcm16Sink;", "", "file", "Ljava/io/File;", "(Ljava/io/File;)V", "bytes", "", "closed", "", "output", "Ljava/io/BufferedOutputStream;", "position", "", "<set-?>", "", "sampleCount", "getSampleCount", "()J", "close", "", "closeQuietly", "flush", "write", "sample", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
final class Pcm16Sink {
    private final byte[] bytes;
    private boolean closed;
    private final BufferedOutputStream output;
    private int position;
    private long sampleCount;

    public Pcm16Sink(File file) {
        Intrinsics.checkNotNullParameter(file, "file");
        this.output = new BufferedOutputStream(new FileOutputStream(file), 131072);
        this.bytes = new byte[65536];
    }

    public final long getSampleCount() {
        return this.sampleCount;
    }

    public final void write(float sample) throws IOException {
        float fCoerceAtLeast;
        int i;
        if (sample >= 0.0f) {
            fCoerceAtLeast = RangesKt.coerceAtMost(sample, 1.0f);
            i = 32767;
        } else {
            fCoerceAtLeast = RangesKt.coerceAtLeast(sample, -1.0f);
            i = 32768;
        }
        int i2 = (int) (fCoerceAtLeast * i);
        if (this.position + 2 > this.bytes.length) {
            flush();
        }
        byte[] bArr = this.bytes;
        int i3 = this.position;
        bArr[i3] = (byte) (i2 & KotlinVersion.MAX_COMPONENT_VALUE);
        this.position = i3 + 2;
        bArr[i3 + 1] = (byte) ((i2 >>> 8) & KotlinVersion.MAX_COMPONENT_VALUE);
        this.sampleCount++;
    }

    public final void close() throws IOException {
        if (this.closed) {
            return;
        }
        flush();
        this.output.close();
        this.closed = true;
    }

    public final void closeQuietly() {
        try {
            close();
        } catch (Throwable unused) {
        }
    }

    private final void flush() throws IOException {
        int i = this.position;
        if (i > 0) {
            this.output.write(this.bytes, 0, i);
        }
        this.position = 0;
    }
}
