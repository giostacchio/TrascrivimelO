package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.view.Surface;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.encoding.Base64;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: AudioDecoder.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J2\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u000eJ \u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/AudioDecoder;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "decode", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/DecodedAudio;", "uri", "Landroid/net/Uri;", "destination", "Ljava/io/File;", "cancelled", "Ljava/util/concurrent/atomic/AtomicBoolean;", "onProgress", "Lkotlin/Function1;", "", "", "pcmToMono", "", "buffer", "Ljava/nio/ByteBuffer;", "channelCount", "encoding", "Companion", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final class AudioDecoder {
    public static final int TARGET_SAMPLE_RATE = 16000;
    private static final long TIMEOUT_US = 10000;
    private final Context context;

    public AudioDecoder(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    /* JADX WARN: Code duplicated, block: B:48:0x0139  */
    /* JADX WARN: Code duplicated, block: B:78:0x01bc  */
    /* JADX WARN: Code duplicated, block: B:79:0x01c0  */
    /* JADX WARN: Multi-variable type inference failed */
    public final DecodedAudio decode(Uri uri, File destination, AtomicBoolean cancelled, Function1<? super Integer, Unit> onProgress) {
        MediaCodec mediaCodec;
        MediaExtractor mediaExtractor;
        MediaCodec mediaCodec2;
        Integer next;
        long j;
        int i;
        LinearResampler linearResampler;
        int i2;
        boolean z;
        String str = "pcm-encoding";
        String str2 = "sample-rate";
        String str3 = "durationUs";
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(destination, "destination");
        Intrinsics.checkNotNullParameter(cancelled, "cancelled");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        MediaExtractor mediaExtractor2 = new MediaExtractor();
        Pcm16Sink pcm16Sink = new Pcm16Sink(destination);
        MediaCodec mediaCodec3 = null;
        mediaCodec3 = null;
        try {
            try {
                mediaExtractor2.setDataSource(this.context, uri, (Map<String, String>) null);
                Iterator<Integer> it = RangesKt.until(0, mediaExtractor2.getTrackCount()).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        next = null;
                        break;
                    }
                    try {
                        next = it.next();
                        String string = mediaExtractor2.getTrackFormat(next.intValue()).getString("mime");
                        if (string != null) {
                            if (StringsKt.startsWith$default(string, "audio/", false, 2, (Object) null)) {
                                break;
                            }
                        }
                        it = it;
                        mediaCodec3 = null;
                    } catch (Throwable th) {
                        th = th;
                        mediaExtractor = mediaExtractor2;
                        mediaCodec2 = 0;
                    }
                }
                try {
                    Integer num = next;
                    try {
                        if (num == null) {
                            throw new IllegalStateException("Il file non contiene una traccia audio leggibile.".toString());
                        }
                        int iIntValue = num.intValue();
                        mediaExtractor2.selectTrack(iIntValue);
                        MediaFormat trackFormat = mediaExtractor2.getTrackFormat(iIntValue);
                        Intrinsics.checkNotNullExpressionValue(trackFormat, "getTrackFormat(...)");
                        String string2 = trackFormat.getString("mime");
                        if (string2 == null) {
                            throw new IllegalStateException("Formato audio non riconosciuto.".toString());
                        }
                        long j2 = trackFormat.containsKey("durationUs") ? trackFormat.getLong("durationUs") : -1L;
                        MediaCodec mediaCodecCreateDecoderByType = MediaCodec.createDecoderByType(string2);
                        Intrinsics.checkNotNullExpressionValue(mediaCodecCreateDecoderByType, "createDecoderByType(...)");
                        try {
                            mediaCodecCreateDecoderByType.configure(trackFormat, (Surface) null, (MediaCrypto) null, 0);
                            mediaCodecCreateDecoderByType.start();
                            int integer = trackFormat.getInteger("channel-count");
                            LinearResampler linearResampler2 = new LinearResampler(trackFormat.getInteger("sample-rate"), TARGET_SAMPLE_RATE, new AudioDecoder$decode$resampler$1(pcm16Sink));
                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int integer2 = 2;
                            boolean z2 = false;
                            boolean z3 = false;
                            int i3 = -1;
                            while (!z3) {
                                if (cancelled.get()) {
                                    throw new CancellationException();
                                }
                                String str4 = str;
                                String str5 = str2;
                                if (z2) {
                                    j = TIMEOUT_US;
                                } else {
                                    try {
                                        int iDequeueInputBuffer = mediaCodecCreateDecoderByType.dequeueInputBuffer(TIMEOUT_US);
                                        if (iDequeueInputBuffer < 0) {
                                            j = TIMEOUT_US;
                                        } else {
                                            ByteBuffer inputBuffer = mediaCodecCreateDecoderByType.getInputBuffer(iDequeueInputBuffer);
                                            if (inputBuffer == null) {
                                                throw new IllegalStateException("Impossibile leggere il buffer audio.".toString());
                                            }
                                            inputBuffer.clear();
                                            int sampleData = mediaExtractor2.readSampleData(inputBuffer, 0);
                                            if (sampleData < 0) {
                                                mediaCodecCreateDecoderByType.queueInputBuffer(iDequeueInputBuffer, 0, 0, 0L, 4);
                                                j = TIMEOUT_US;
                                                z2 = true;
                                            } else {
                                                mediaCodecCreateDecoderByType.queueInputBuffer(iDequeueInputBuffer, 0, sampleData, mediaExtractor2.getSampleTime(), 0);
                                                mediaExtractor2.advance();
                                                j = TIMEOUT_US;
                                            }
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                        mediaCodec = mediaCodecCreateDecoderByType;
                                        mediaExtractor = mediaExtractor2;
                                        mediaCodec2 = mediaCodec;
                                        pcm16Sink.closeQuietly();
                                        destination.delete();
                                        throw th;
                                    }
                                }
                                int iDequeueOutputBuffer = mediaCodecCreateDecoderByType.dequeueOutputBuffer(bufferInfo, j);
                                if (iDequeueOutputBuffer != -3) {
                                    if (iDequeueOutputBuffer == -2) {
                                        LinearResampler linearResampler3 = linearResampler2;
                                        MediaExtractor mediaExtractor3 = mediaExtractor2;
                                        int i4 = i3;
                                        MediaFormat outputFormat = mediaCodecCreateDecoderByType.getOutputFormat();
                                        Intrinsics.checkNotNullExpressionValue(outputFormat, "getOutputFormat(...)");
                                        int integer3 = outputFormat.getInteger("channel-count");
                                        integer2 = outputFormat.containsKey(str4) ? outputFormat.getInteger(str4) : 2;
                                        if (pcm16Sink.getSampleCount() == 0) {
                                            LinearResampler linearResampler4 = new LinearResampler(outputFormat.getInteger(str5), TARGET_SAMPLE_RATE, new AnonymousClass1(pcm16Sink));
                                            integer = integer3;
                                            str = str4;
                                            linearResampler2 = linearResampler4;
                                            str2 = str5;
                                            i3 = i4;
                                            mediaExtractor2 = mediaExtractor3;
                                            bufferInfo = bufferInfo;
                                        } else {
                                            integer = integer3;
                                            str = str4;
                                            i3 = i4;
                                            mediaExtractor2 = mediaExtractor3;
                                            linearResampler2 = linearResampler3;
                                        }
                                    } else if (iDequeueOutputBuffer != -1 && iDequeueOutputBuffer >= 0) {
                                        if (bufferInfo.size > 0 && (bufferInfo.flags & 2) == 0) {
                                            ByteBuffer outputBuffer = mediaCodecCreateDecoderByType.getOutputBuffer(iDequeueOutputBuffer);
                                            if (outputBuffer == null) {
                                                throw new IllegalStateException("Impossibile decodificare il buffer audio.".toString());
                                            }
                                            outputBuffer.position(bufferInfo.offset);
                                            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                                            outputBuffer.order(ByteOrder.LITTLE_ENDIAN);
                                            linearResampler2.accept(pcmToMono(outputBuffer, integer, integer2));
                                        }
                                        if (j2 > 0) {
                                            i = integer;
                                            linearResampler = linearResampler2;
                                            mediaExtractor = mediaExtractor2;
                                            try {
                                                int iMin = Math.min(100, Math.max(0, (int) ((bufferInfo.presentationTimeUs * ((long) 100)) / j2)));
                                                i2 = i3;
                                                if (iMin != i2) {
                                                    onProgress.invoke(Integer.valueOf(iMin));
                                                    i3 = iMin;
                                                }
                                                if ((bufferInfo.flags & 4) != 0) {
                                                    z = false;
                                                    z3 = true;
                                                } else {
                                                    z = false;
                                                    z3 = false;
                                                }
                                                mediaCodecCreateDecoderByType.releaseOutputBuffer(iDequeueOutputBuffer, z);
                                                integer = i;
                                                mediaExtractor2 = mediaExtractor;
                                                linearResampler2 = linearResampler;
                                                str = str4;
                                            } catch (Throwable th3) {
                                                th = th3;
                                            }
                                        } else {
                                            i = integer;
                                            linearResampler = linearResampler2;
                                            mediaExtractor = mediaExtractor2;
                                            i2 = i3;
                                        }
                                        i3 = i2;
                                        if ((bufferInfo.flags & 4) != 0) {
                                            z = false;
                                            z3 = true;
                                        } else {
                                            z = false;
                                            z3 = false;
                                        }
                                        mediaCodecCreateDecoderByType.releaseOutputBuffer(iDequeueOutputBuffer, z);
                                        integer = i;
                                        mediaExtractor2 = mediaExtractor;
                                        linearResampler2 = linearResampler;
                                        str = str4;
                                    }
                                    str2 = str5;
                                }
                                integer = integer;
                                str = str4;
                                str2 = str5;
                                i3 = i3;
                                mediaExtractor2 = mediaExtractor2;
                                linearResampler2 = linearResampler2;
                                bufferInfo = bufferInfo;
                                mediaCodec2 = mediaCodecCreateDecoderByType;
                                pcm16Sink.closeQuietly();
                                destination.delete();
                                throw th;
                            }
                            MediaExtractor mediaExtractor4 = mediaExtractor2;
                            pcm16Sink.close();
                            if (pcm16Sink.getSampleCount() == 0) {
                                throw new IllegalStateException("Il file audio è vuoto.".toString());
                            }
                            onProgress.invoke(100);
                            DecodedAudio decodedAudio = new DecodedAudio(destination, pcm16Sink.getSampleCount(), pcm16Sink.getSampleCount() / ((double) TARGET_SAMPLE_RATE));
                            try {
                                mediaCodecCreateDecoderByType.stop();
                            } catch (Throwable unused) {
                            }
                            try {
                                mediaCodecCreateDecoderByType.release();
                            } catch (Throwable unused2) {
                            }
                            mediaExtractor4.release();
                            return decodedAudio;
                        } catch (Throwable th4) {
                            th = th4;
                            mediaExtractor = mediaExtractor2;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        mediaCodec2 = str3;
                    }
                } catch (Throwable th6) {
                    th = th6;
                    mediaExtractor = mediaExtractor2;
                    str3 = null;
                }
                mediaCodec2 = str3;
            } catch (Throwable th7) {
                th = th7;
                mediaCodec = mediaCodec3;
            }
            pcm16Sink.closeQuietly();
            destination.delete();
            throw th;
        } catch (Throwable th8) {
            if (mediaCodec2 != 0) {
                try {
                    mediaCodec2.stop();
                } catch (Throwable unused3) {
                }
            }
            if (mediaCodec2 != 0) {
                try {
                    mediaCodec2.release();
                } catch (Throwable unused4) {
                }
            }
            mediaExtractor.release();
            throw th8;
        }
    }

    /* JADX INFO: renamed from: com.k2fsa.sherpa.onnx.speaker.diarization.AudioDecoder$decode$1, reason: invalid class name */
    /* JADX INFO: compiled from: AudioDecoder.kt */
    @Metadata(k = Base64.bytesPerGroup, mv = {1, 9, 0}, xi = 48)
    /* synthetic */ class AnonymousClass1 extends FunctionReferenceImpl implements Function1<Float, Unit> {
        AnonymousClass1(Object obj) {
            super(1, obj, Pcm16Sink.class, "write", "write(F)V", 0);
        }

        @Override // kotlin.jvm.functions.Function1
        public /* bridge */ /* synthetic */ Unit invoke(Float f) throws IOException {
            invoke(f.floatValue());
            return Unit.INSTANCE;
        }

        public final void invoke(float f) throws IOException {
            ((Pcm16Sink) this.receiver).write(f);
        }
    }

    private final float[] pcmToMono(ByteBuffer buffer, int channelCount, int encoding) {
        float f;
        int iMax = Math.max(1, channelCount);
        int iRemaining = buffer.remaining() / ((encoding == 4 ? 4 : 2) * iMax);
        float[] fArr = new float[iRemaining];
        for (int i = 0; i < iRemaining; i++) {
            float f2 = 0.0f;
            for (int i2 = 0; i2 < iMax; i2++) {
                if (encoding == 4) {
                    f = buffer.getFloat();
                } else {
                    f = buffer.getShort() / 32768.0f;
                }
                f2 += f;
            }
            fArr[i] = RangesKt.coerceIn(f2 / iMax, -1.0f, 1.0f);
        }
        return fArr;
    }
}
