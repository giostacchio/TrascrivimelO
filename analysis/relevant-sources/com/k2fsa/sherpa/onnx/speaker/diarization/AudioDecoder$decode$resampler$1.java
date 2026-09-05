package com.k2fsa.sherpa.onnx.speaker.diarization;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.encoding.Base64;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* JADX INFO: compiled from: AudioDecoder.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(k = Base64.bytesPerGroup, mv = {1, 9, 0}, xi = 48)
/* synthetic */ class AudioDecoder$decode$resampler$1 extends FunctionReferenceImpl implements Function1<Float, Unit> {
    AudioDecoder$decode$resampler$1(Object obj) {
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
