package com.k2fsa.sherpa.onnx.speaker.diarization;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.encoding.Base64;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(k = Base64.bytesPerGroup, mv = {1, 9, 0}, xi = 48)
/* synthetic */ class MainActivity$startTranscription$1$lines$1 extends FunctionReferenceImpl implements Function2<Integer, String, Unit> {
    MainActivity$startTranscription$1$lines$1(Object obj) {
        super(2, obj, MainActivity.class, "updateProgress", "updateProgress(ILjava/lang/String;)V", 0);
    }

    @Override // kotlin.jvm.functions.Function2
    public /* bridge */ /* synthetic */ Unit invoke(Integer num, String str) {
        invoke(num.intValue(), str);
        return Unit.INSTANCE;
    }

    public final void invoke(int i, String p1) {
        Intrinsics.checkNotNullParameter(p1, "p1");
        ((MainActivity) this.receiver).updateProgress(i, p1);
    }
}
