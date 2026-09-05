package com.k2fsa.sherpa.onnx.speaker.diarization;

import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AudioDecoder.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0007HÆ\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/DecodedAudio;", "", "pcmFile", "Ljava/io/File;", "sampleCount", "", "durationSeconds", "", "(Ljava/io/File;JD)V", "getDurationSeconds", "()D", "getPcmFile", "()Ljava/io/File;", "getSampleCount", "()J", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final /* data */ class DecodedAudio {
    private final double durationSeconds;
    private final File pcmFile;
    private final long sampleCount;

    public static /* synthetic */ DecodedAudio copy$default(DecodedAudio decodedAudio, File file, long j, double d, int i, Object obj) {
        if ((i & 1) != 0) {
            file = decodedAudio.pcmFile;
        }
        if ((i & 2) != 0) {
            j = decodedAudio.sampleCount;
        }
        long j2 = j;
        if ((i & 4) != 0) {
            d = decodedAudio.durationSeconds;
        }
        return decodedAudio.copy(file, j2, d);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final File getPcmFile() {
        return this.pcmFile;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final long getSampleCount() {
        return this.sampleCount;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final double getDurationSeconds() {
        return this.durationSeconds;
    }

    public final DecodedAudio copy(File pcmFile, long sampleCount, double durationSeconds) {
        Intrinsics.checkNotNullParameter(pcmFile, "pcmFile");
        return new DecodedAudio(pcmFile, sampleCount, durationSeconds);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DecodedAudio)) {
            return false;
        }
        DecodedAudio decodedAudio = (DecodedAudio) other;
        return Intrinsics.areEqual(this.pcmFile, decodedAudio.pcmFile) && this.sampleCount == decodedAudio.sampleCount && Double.compare(this.durationSeconds, decodedAudio.durationSeconds) == 0;
    }

    public int hashCode() {
        return (((this.pcmFile.hashCode() * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.sampleCount)) * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.durationSeconds);
    }

    public String toString() {
        return "DecodedAudio(pcmFile=" + this.pcmFile + ", sampleCount=" + this.sampleCount + ", durationSeconds=" + this.durationSeconds + ')';
    }

    public DecodedAudio(File pcmFile, long j, double d) {
        Intrinsics.checkNotNullParameter(pcmFile, "pcmFile");
        this.pcmFile = pcmFile;
        this.sampleCount = j;
        this.durationSeconds = d;
    }

    public final File getPcmFile() {
        return this.pcmFile;
    }

    public final long getSampleCount() {
        return this.sampleCount;
    }

    public final double getDurationSeconds() {
        return this.durationSeconds;
    }
}
