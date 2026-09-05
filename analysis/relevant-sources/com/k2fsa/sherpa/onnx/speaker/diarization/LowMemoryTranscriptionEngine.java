package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.content.res.AssetManager;
import com.k2fsa.sherpa.onnx.FastClusteringConfig;
import com.k2fsa.sherpa.onnx.FeatureConfig;
import com.k2fsa.sherpa.onnx.OfflineModelConfig;
import com.k2fsa.sherpa.onnx.OfflineParaformerModelConfig;
import com.k2fsa.sherpa.onnx.OfflineRecognizer;
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarization;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationSegment;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationModelConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationPyannoteModelConfig;
import com.k2fsa.sherpa.onnx.OfflineStream;
import com.k2fsa.sherpa.onnx.OfflineTransducerModelConfig;
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig;
import com.k2fsa.sherpa.onnx.OnlineStream;
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractor;
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractorConfig;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.UByte;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.sequences.SequencesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000¤\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\u0018\u0000 O2\u00020\u0001:\u0006NOPQRSB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004JN\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u000f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0018\u0010\u0019\u001a\u0014\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u001aH\u0002J8\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u000f2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000f2\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160\"2\u0006\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020\u001c2\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)H\u0002J@\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00140\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0018\u0010\u0019\u001a\u0014\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u001aH\u0002J\u0010\u0010,\u001a\u00020\u001c2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010-\u001a\u00020\u001cH\u0002J\b\u0010.\u001a\u00020\u001cH\u0002J2\u0010/\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160\"2\u0006\u00100\u001a\u00020)2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u00101\u001a\u00020)2\u0006\u00102\u001a\u00020)H\u0002J \u00103\u001a\u00020\u00162\u0006\u00104\u001a\u00020\u00162\u0006\u00105\u001a\u00020\u00162\u0006\u00106\u001a\u00020$H\u0002J \u00107\u001a\u00020)2\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020\u0016H\u0002J\u0010\u0010=\u001a\u00020\u001b2\u0006\u0010>\u001a\u00020)H\u0002JZ\u0010?\u001a\b\u0012\u0004\u0012\u00020@0\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010A\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0017\u001a\u00020\u00182\u0018\u0010\u0019\u001a\u0014\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u001a2\u0012\u0010B\u001a\u000e\u0012\u0004\u0012\u00020@\u0012\u0004\u0012\u00020\u001c0CH\u0002J\u0006\u0010D\u001a\u00020\u001cJ\b\u0010E\u001a\u00020\u001cH\u0002J\b\u0010F\u001a\u00020\u001cH\u0002J\b\u0010G\u001a\u00020\u001cH\u0002J\b\u0010H\u001a\u00020\u001cH\u0002JR\u0010I\u001a\b\u0012\u0004\u0012\u00020@0\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0018\u0010\u0019\u001a\u0014\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u001c0\u001a2\u0012\u0010B\u001a\u000e\u0012\u0004\u0012\u00020@\u0012\u0004\u0012\u00020\u001c0CJ\u0018\u0010J\u001a\u00020\u001c2\u0006\u0010K\u001a\u00020\u00162\u0006\u0010L\u001a\u00020)H\u0002J \u0010M\u001a\u0004\u0018\u00010)2\u0006\u00100\u001a\u00020)2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006T"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine;", "", "assets", "Landroid/content/res/AssetManager;", "(Landroid/content/res/AssetManager;)V", "diarizer", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarization;", "extractor", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;", "profiles", "", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$VoiceProfile;", "recognizer", "Lcom/k2fsa/sherpa/onnx/OfflineRecognizer;", "assignSpeakers", "", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$PendingTurn;", "audio", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/DecodedAudio;", "chunks", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$ChunkPlan;", "requestedSpeakers", "", "cancelled", "Ljava/util/concurrent/atomic/AtomicBoolean;", "onProgress", "Lkotlin/Function2;", "", "", "buildTurns", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$Turn;", "segments", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$Segment;", "speakerMap", "", "duration", "", "checkCancelled", "cosine", "", "a", "", "b", "diarize", "initializeDiarizer", "initializeExtractor", "initializeRecognizer", "matchSpeakers", "samples", "normalize", "values", "progress", "base", "width", "fraction", "readPcm16", "file", "Ljava/io/File;", "firstSample", "", "count", "recognize", "waveform", "recognizeTurns", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptLine;", "turns", "onLine", "Lkotlin/Function1;", "release", "releaseDiarizer", "releaseExtractor", "releaseRecognizer", "requestMemoryCleanup", "transcribe", "updateProfile", "id", "embedding", "voiceEmbedding", "ChunkPlan", "Companion", "PendingTurn", "Segment", "Turn", "VoiceProfile", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final class LowMemoryTranscriptionEngine {
    private static final int CHUNK_SECONDS = 240;
    private static final double MAX_TURN_SECONDS = 28.0d;
    private static final double PROFILE_SECONDS = 12.0d;
    private static final int SAMPLE_RATE = 16000;
    private final AssetManager assets;
    private OfflineSpeakerDiarization diarizer;
    private SpeakerEmbeddingExtractor extractor;
    private final List<VoiceProfile> profiles;
    private OfflineRecognizer recognizer;

    public LowMemoryTranscriptionEngine(AssetManager assets) {
        Intrinsics.checkNotNullParameter(assets, "assets");
        this.assets = assets;
        this.profiles = new ArrayList();
    }

    public final List<TranscriptLine> transcribe(DecodedAudio audio, int requestedSpeakers, AtomicBoolean cancelled, Function2<? super Integer, ? super String, Unit> onProgress, Function1<? super TranscriptLine, Unit> onLine) throws Throwable {
        Intrinsics.checkNotNullParameter(audio, "audio");
        Intrinsics.checkNotNullParameter(cancelled, "cancelled");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        Intrinsics.checkNotNullParameter(onLine, "onLine");
        checkCancelled(cancelled);
        List<ChunkPlan> listDiarize = diarize(audio, requestedSpeakers, cancelled, onProgress);
        checkCancelled(cancelled);
        List<PendingTurn> listAssignSpeakers = assignSpeakers(audio, listDiarize, requestedSpeakers, cancelled, onProgress);
        checkCancelled(cancelled);
        List<TranscriptLine> listRecognizeTurns = recognizeTurns(audio, listAssignSpeakers, cancelled, onProgress, onLine);
        checkCancelled(cancelled);
        onProgress.invoke(99, "Chiusura e verifica del file TXT…");
        return listRecognizeTurns;
    }

    public final void release() {
        releaseRecognizer();
        releaseDiarizer();
        releaseExtractor();
        this.profiles.clear();
    }

    private final List<ChunkPlan> diarize(DecodedAudio audio, int requestedSpeakers, final AtomicBoolean cancelled, final Function2<? super Integer, ? super String, Unit> onProgress) throws Throwable {
        String string;
        int i = 20;
        onProgress.invoke(20, "Caricamento del modello per distinguere le voci…");
        initializeDiarizer(requestedSpeakers);
        long j = 3840000;
        int i2 = 1;
        final int iCoerceAtLeast = RangesKt.coerceAtLeast((int) Math.ceil(audio.getSampleCount() / 3840000), 1);
        ArrayList arrayList = new ArrayList(iCoerceAtLeast);
        int i3 = 0;
        while (true) {
            String str = "Cambio del modello offline…";
            if (i3 >= iCoerceAtLeast) {
                onProgress.invoke(49, "Cambio del modello offline…");
                releaseDiarizer();
                requestMemoryCleanup();
                return arrayList;
            }
            try {
                checkCancelled(cancelled);
                long j2 = ((long) i3) * j;
                int iMin = (int) Math.min(j, audio.getSampleCount() - j2);
                float[] pcm16 = readPcm16(audio.getPcmFile(), j2, iMin);
                if (iCoerceAtLeast > i2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(' ');
                    sb.append(i3 + 1);
                    sb.append('/');
                    sb.append(iCoerceAtLeast);
                    string = sb.toString();
                } else {
                    string = "";
                }
                final String str2 = string;
                onProgress.invoke(Integer.valueOf(progress(i, 28, ((double) i3) / ((double) iCoerceAtLeast))), "Distinzione delle voci" + str2 + Typography.ellipsis);
                OfflineSpeakerDiarization offlineSpeakerDiarization = this.diarizer;
                if (offlineSpeakerDiarization == null) {
                    throw new IllegalArgumentException("Required value was null.".toString());
                }
                final int i4 = i3;
                int i5 = i3;
                try {
                    OfflineSpeakerDiarizationSegment[] offlineSpeakerDiarizationSegmentArrProcessWithCallback$default = OfflineSpeakerDiarization.processWithCallback$default(offlineSpeakerDiarization, pcm16, new Function3<Integer, Integer, Long, Integer>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$diarize$nativeSegments$1
                        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                        /* JADX WARN: Multi-variable type inference failed */
                        {
                            super(3);
                        }

                        @Override // kotlin.jvm.functions.Function3
                        public /* bridge */ /* synthetic */ Integer invoke(Integer num, Integer num2, Long l) {
                            return invoke(num.intValue(), num2.intValue(), l.longValue());
                        }

                        public final Integer invoke(int i6, int i7, long j3) {
                            int i8;
                            if (cancelled.get()) {
                                i8 = 1;
                            } else {
                                double d = i7 > 0 ? ((double) i6) / ((double) i7) : 0.0d;
                                onProgress.invoke(Integer.valueOf(this.progress(20, 28, (((double) i4) + d) / ((double) iCoerceAtLeast))), "Distinzione delle voci" + str2 + Typography.ellipsis);
                                i8 = 0;
                            }
                            return Integer.valueOf(i8);
                        }
                    }, 0L, 4, null);
                    checkCancelled(cancelled);
                    arrayList.add(new ChunkPlan(j2, iMin, SequencesKt.toList(SequencesKt.sortedWith(SequencesKt.map(SequencesKt.filter(ArraysKt.asSequence(offlineSpeakerDiarizationSegmentArrProcessWithCallback$default), new Function1<OfflineSpeakerDiarizationSegment, Boolean>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$diarize$segments$1
                        @Override // kotlin.jvm.functions.Function1
                        public final Boolean invoke(OfflineSpeakerDiarizationSegment it) {
                            Intrinsics.checkNotNullParameter(it, "it");
                            return Boolean.valueOf(it.getEnd() > it.getStart() && it.getEnd() > 0.0f);
                        }
                    }), new Function1<OfflineSpeakerDiarizationSegment, Segment>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$diarize$segments$2
                        @Override // kotlin.jvm.functions.Function1
                        public final LowMemoryTranscriptionEngine.Segment invoke(OfflineSpeakerDiarizationSegment it) {
                            Intrinsics.checkNotNullParameter(it, "it");
                            return new LowMemoryTranscriptionEngine.Segment(it.getStart(), it.getEnd(), it.getSpeaker());
                        }
                    }), new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$diarize$$inlined$sortedBy$1
                        /* JADX WARN: Multi-variable type inference failed */
                        @Override // java.util.Comparator
                        public final int compare(T t, T t2) {
                            return ComparisonsKt.compareValues(Double.valueOf(((LowMemoryTranscriptionEngine.Segment) t).getStart()), Double.valueOf(((LowMemoryTranscriptionEngine.Segment) t2).getStart()));
                        }
                    }))));
                    i3 = i5 + 1;
                    i = 20;
                    j = 3840000;
                    i2 = 1;
                } catch (Throwable th) {
                    th = th;
                }
                th = th;
            } catch (Throwable th2) {
                th = th2;
                str = "Cambio del modello offline…";
            }
            onProgress.invoke(49, str);
            releaseDiarizer();
            requestMemoryCleanup();
            throw th;
        }
    }

    private final List<PendingTurn> assignSpeakers(DecodedAudio audio, List<ChunkPlan> chunks, int requestedSpeakers, AtomicBoolean cancelled, Function2<? super Integer, ? super String, Unit> onProgress) throws Throwable {
        LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine;
        LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine2 = this;
        List<ChunkPlan> list = chunks;
        if (!(list instanceof Collection) || !list.isEmpty()) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                if (!((ChunkPlan) it.next()).getSegments().isEmpty()) {
                    onProgress.invoke(50, "Caricamento del modello per riconoscere le persone…");
                    initializeExtractor();
                    lowMemoryTranscriptionEngine2.profiles.clear();
                    ArrayList arrayList = new ArrayList();
                    try {
                        int i = 0;
                        for (Iterator it2 = chunks.iterator(); it2.hasNext(); it2 = it2) {
                            Object next = it2.next();
                            int i2 = i + 1;
                            if (i < 0) {
                                CollectionsKt.throwIndexOverflow();
                            }
                            ChunkPlan chunkPlan = (ChunkPlan) next;
                            lowMemoryTranscriptionEngine2.checkCancelled(cancelled);
                            if (!chunkPlan.getSegments().isEmpty()) {
                                Map<Integer, Integer> mapMatchSpeakers = lowMemoryTranscriptionEngine2.matchSpeakers(lowMemoryTranscriptionEngine2.readPcm16(audio.getPcmFile(), chunkPlan.getFirstSample(), chunkPlan.getSampleCount()), chunkPlan.getSegments(), requestedSpeakers);
                                double d = 16000;
                                double firstSample = chunkPlan.getFirstSample() / d;
                                try {
                                    lowMemoryTranscriptionEngine = this;
                                    try {
                                        for (Turn turn : lowMemoryTranscriptionEngine.buildTurns(chunkPlan.getSegments(), mapMatchSpeakers, ((double) chunkPlan.getSampleCount()) / d)) {
                                            arrayList.add(new PendingTurn(turn.getSpeaker(), firstSample + turn.getStart(), firstSample + turn.getEnd()));
                                        }
                                    } catch (Throwable th) {
                                        th = th;
                                        onProgress.invoke(64, "Cambio del modello offline…");
                                        releaseExtractor();
                                        lowMemoryTranscriptionEngine.profiles.clear();
                                        requestMemoryCleanup();
                                        throw th;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    lowMemoryTranscriptionEngine = this;
                                }
                            } else {
                                lowMemoryTranscriptionEngine = lowMemoryTranscriptionEngine2;
                            }
                            onProgress.invoke(Integer.valueOf(lowMemoryTranscriptionEngine.progress(50, 14, ((double) i2) / ((double) RangesKt.coerceAtLeast(chunks.size(), 1)))), "Confronto delle voci " + i2 + '/' + chunks.size() + Typography.ellipsis);
                            lowMemoryTranscriptionEngine2 = lowMemoryTranscriptionEngine;
                            i = i2;
                        }
                        onProgress.invoke(64, "Cambio del modello offline…");
                        releaseExtractor();
                        lowMemoryTranscriptionEngine2.profiles.clear();
                        requestMemoryCleanup();
                        return arrayList;
                    } catch (Throwable th3) {
                        th = th3;
                        lowMemoryTranscriptionEngine = lowMemoryTranscriptionEngine2;
                    }
                }
            }
        }
        return CollectionsKt.emptyList();
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0103  */
    private final List<TranscriptLine> recognizeTurns(DecodedAudio audio, List<PendingTurn> turns, AtomicBoolean cancelled, Function2<? super Integer, ? super String, Unit> onProgress, Function1<? super TranscriptLine, Unit> onLine) {
        if (turns.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        onProgress.invoke(65, "Caricamento di Whisper italiano…");
        initializeRecognizer();
        ArrayList arrayList = new ArrayList(turns.size());
        try {
            int i = 0;
            for (Object obj : turns) {
                int i2 = i + 1;
                if (i < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                PendingTurn pendingTurn = (PendingTurn) obj;
                checkCancelled(cancelled);
                double dMax = Math.max(0.0d, pendingTurn.getStart() - 0.1d);
                double dMin = Math.min(audio.getDurationSeconds(), 0.1d + pendingTurn.getEnd());
                double d = 16000;
                ArrayList arrayList2 = arrayList;
                long jCoerceAtLeast = RangesKt.coerceAtLeast((long) (dMax * d), 0L);
                long jCoerceAtMost = RangesKt.coerceAtMost((long) (dMin * d), audio.getSampleCount());
                onProgress.invoke(Integer.valueOf(progress(65, 33, ((double) i) / ((double) turns.size()))), "Trascrizione " + i2 + '/' + turns.size() + " — Persona " + (pendingTurn.getSpeaker() + 1) + Typography.ellipsis);
                long j = jCoerceAtMost - jCoerceAtLeast;
                if (j >= 7200) {
                    String strRecognize = recognize(readPcm16(audio.getPcmFile(), jCoerceAtLeast, (int) j));
                    if (!StringsKt.isBlank(strRecognize)) {
                        TranscriptLine transcriptLine = new TranscriptLine(pendingTurn.getSpeaker() + 1, pendingTurn.getStart(), pendingTurn.getEnd(), strRecognize);
                        arrayList2.add(transcriptLine);
                        onLine.invoke(transcriptLine);
                    }
                }
                i = i2;
                arrayList = arrayList2;
            }
            return arrayList;
        } finally {
            releaseRecognizer();
            requestMemoryCleanup();
        }
    }

    private final void initializeRecognizer() {
        if (this.recognizer != null) {
            throw new IllegalStateException("Check failed.".toString());
        }
        OfflineTransducerModelConfig offlineTransducerModelConfig = null;
        OfflineParaformerModelConfig offlineParaformerModelConfig = null;
        this.recognizer = new OfflineRecognizer(this.assets, new OfflineRecognizerConfig(new FeatureConfig(16000, 80, 0.0f, 4, null), new OfflineModelConfig(offlineTransducerModelConfig, offlineParaformerModelConfig, new OfflineWhisperModelConfig("sherpa-onnx-whisper-tiny/tiny-encoder.int8.onnx", "sherpa-onnx-whisper-tiny/tiny-decoder.int8.onnx", "it", "transcribe", 1000, false, false, 96, null), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 2, false, "cpu", "whisper", "sherpa-onnx-whisper-tiny/tiny-tokens.txt", null, null, 25427963, null), null, "greedy_search", 0, null, 0.0f, null, null, 0.0f, 1012, null));
    }

    private final void initializeDiarizer(int requestedSpeakers) {
        if (this.diarizer != null) {
            throw new IllegalStateException("Check failed.".toString());
        }
        AssetManager assetManager = this.assets;
        OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig = new OfflineSpeakerSegmentationModelConfig(new OfflineSpeakerSegmentationPyannoteModelConfig("segmentation.onnx", 0.1f), 2, false, "cpu");
        SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig = new SpeakerEmbeddingExtractorConfig("embedding.onnx", 2, false, "cpu");
        if (requestedSpeakers <= 0) {
            requestedSpeakers = -1;
        }
        this.diarizer = new OfflineSpeakerDiarization(assetManager, new OfflineSpeakerDiarizationConfig(offlineSpeakerSegmentationModelConfig, speakerEmbeddingExtractorConfig, new FastClusteringConfig(requestedSpeakers, 0.5f), 0.2f, 0.5f));
    }

    private final void initializeExtractor() {
        if (this.extractor != null) {
            throw new IllegalStateException("Check failed.".toString());
        }
        this.extractor = new SpeakerEmbeddingExtractor(this.assets, new SpeakerEmbeddingExtractorConfig("embedding.onnx", 2, false, "cpu"));
    }

    private final void releaseRecognizer() {
        OfflineRecognizer offlineRecognizer = this.recognizer;
        this.recognizer = null;
        if (offlineRecognizer != null) {
            try {
                Result.Companion companion = Result.INSTANCE;
                LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine = this;
                offlineRecognizer.release();
                Result.m6constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                Result.Companion companion2 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th));
            }
        }
    }

    private final void releaseDiarizer() {
        OfflineSpeakerDiarization offlineSpeakerDiarization = this.diarizer;
        this.diarizer = null;
        if (offlineSpeakerDiarization != null) {
            try {
                Result.Companion companion = Result.INSTANCE;
                LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine = this;
                offlineSpeakerDiarization.release();
                Result.m6constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                Result.Companion companion2 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th));
            }
        }
    }

    private final void releaseExtractor() {
        SpeakerEmbeddingExtractor speakerEmbeddingExtractor = this.extractor;
        this.extractor = null;
        if (speakerEmbeddingExtractor != null) {
            try {
                Result.Companion companion = Result.INSTANCE;
                LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine = this;
                speakerEmbeddingExtractor.release();
                Result.m6constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                Result.Companion companion2 = Result.INSTANCE;
                Result.m6constructorimpl(ResultKt.createFailure(th));
            }
        }
    }

    private final void requestMemoryCleanup() {
        try {
            Result.Companion companion = Result.INSTANCE;
            LowMemoryTranscriptionEngine lowMemoryTranscriptionEngine = this;
            Runtime.getRuntime().gc();
            Result.m6constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m6constructorimpl(ResultKt.createFailure(th));
        }
    }

    private final String recognize(float[] waveform) {
        OfflineRecognizer offlineRecognizer = this.recognizer;
        if (offlineRecognizer == null) {
            throw new IllegalArgumentException("Required value was null.".toString());
        }
        OfflineStream offlineStreamCreateStream = offlineRecognizer.createStream();
        try {
            offlineStreamCreateStream.acceptWaveform(waveform, 16000);
            offlineRecognizer.decode(offlineStreamCreateStream);
            return StringsKt.trim((CharSequence) new Regex("\\s+").replace(offlineRecognizer.getResult(offlineStreamCreateStream).getText(), " ")).toString();
        } finally {
            offlineStreamCreateStream.release();
        }
    }

    /* JADX WARN: Code duplicated, block: B:97:0x02c6  */
    private final Map<Integer, Integer> matchSpeakers(float[] samples, List<Segment> segments, int requestedSpeakers) {
        Integer next;
        Integer next2;
        int iIntValue;
        List<Segment> list = segments;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(((Segment) it.next()).getSpeaker()));
        }
        List listDistinct = CollectionsKt.distinct(arrayList);
        List list2 = listDistinct;
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(list2, 10)), 16));
        for (Object obj : list2) {
            LinkedHashMap linkedHashMap2 = linkedHashMap;
            int iIntValue2 = ((Number) obj).intValue();
            ArrayList arrayList2 = new ArrayList();
            for (Object obj2 : list) {
                if (((Segment) obj2).getSpeaker() == iIntValue2) {
                    arrayList2.add(obj2);
                }
            }
            linkedHashMap2.put(obj, voiceEmbedding(samples, arrayList2));
        }
        LinkedHashMap linkedHashMap3 = linkedHashMap;
        if (!this.profiles.isEmpty()) {
            ArrayList arrayList3 = new ArrayList();
            Iterator it2 = listDistinct.iterator();
            while (it2.hasNext()) {
                int iIntValue3 = ((Number) it2.next()).intValue();
                float[] fArr = (float[]) linkedHashMap3.get(Integer.valueOf(iIntValue3));
                if (fArr != null) {
                    int i = 0;
                    for (Object obj3 : this.profiles) {
                        int i2 = i + 1;
                        if (i < 0) {
                            CollectionsKt.throwIndexOverflow();
                        }
                        VoiceProfile voiceProfile = (VoiceProfile) obj3;
                        if (!(voiceProfile.getEmbedding().length == 0)) {
                            arrayList3.add(new Candidate(iIntValue3, i, cosine(fArr, voiceProfile.getEmbedding())));
                        }
                        i = i2;
                    }
                }
            }
            LinkedHashMap linkedHashMap4 = new LinkedHashMap();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            for (Candidate candidate : CollectionsKt.sortedWith(arrayList3, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$matchSpeakers$$inlined$sortedByDescending$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.Comparator
                public final int compare(T t, T t2) {
                    return ComparisonsKt.compareValues(Float.valueOf(((LowMemoryTranscriptionEngine.Candidate) t2).getScore()), Float.valueOf(((LowMemoryTranscriptionEngine.Candidate) t).getScore()));
                }
            })) {
                if (!linkedHashMap4.containsKey(Integer.valueOf(candidate.getLocal())) && !linkedHashSet.contains(Integer.valueOf(candidate.getGlobal())) && (requestedSpeakers > 0 || candidate.getScore() >= 0.42f)) {
                    linkedHashMap4.put(Integer.valueOf(candidate.getLocal()), Integer.valueOf(candidate.getGlobal()));
                    linkedHashSet.add(Integer.valueOf(candidate.getGlobal()));
                }
            }
            int i3 = requestedSpeakers > 0 ? requestedSpeakers : 8;
            Iterator it3 = listDistinct.iterator();
            while (it3.hasNext()) {
                int iIntValue4 = ((Number) it3.next()).intValue();
                if (!linkedHashMap4.containsKey(Integer.valueOf(iIntValue4))) {
                    Iterator<Integer> it4 = CollectionsKt.getIndices(this.profiles).iterator();
                    do {
                        next = null;
                        if (!it4.hasNext()) {
                            next2 = null;
                            break;
                        }
                        next2 = it4.next();
                    } while (!(!linkedHashSet.contains(Integer.valueOf(next2.intValue()))));
                    Integer num = next2;
                    if (this.profiles.size() < i3) {
                        this.profiles.add(new VoiceProfile(new float[0], 0));
                        iIntValue = CollectionsKt.getLastIndex(this.profiles);
                    } else if (num != null) {
                        iIntValue = num.intValue();
                    } else {
                        float[] fArr2 = (float[]) linkedHashMap3.get(Integer.valueOf(iIntValue4));
                        if (fArr2 != null) {
                            Iterator<Integer> it5 = CollectionsKt.getIndices(this.profiles).iterator();
                            if (it5.hasNext()) {
                                next = it5.next();
                                if (it5.hasNext()) {
                                    float fCosine = cosine(fArr2, this.profiles.get(next.intValue()).getEmbedding());
                                    do {
                                        Integer next3 = it5.next();
                                        float fCosine2 = cosine(fArr2, this.profiles.get(next3.intValue()).getEmbedding());
                                        if (Float.compare(fCosine, fCosine2) < 0) {
                                            next = next3;
                                            fCosine = fCosine2;
                                        }
                                    } while (it5.hasNext());
                                }
                            }
                            Integer num2 = next;
                            if (num2 != null) {
                                iIntValue = num2.intValue();
                            } else {
                                iIntValue = 0;
                            }
                        } else {
                            iIntValue = 0;
                        }
                    }
                    linkedHashMap4.put(Integer.valueOf(iIntValue4), Integer.valueOf(iIntValue));
                    linkedHashSet.add(Integer.valueOf(iIntValue));
                }
            }
            for (Map.Entry entry : linkedHashMap4.entrySet()) {
                int iIntValue5 = ((Number) entry.getKey()).intValue();
                int iIntValue6 = ((Number) entry.getValue()).intValue();
                float[] fArr3 = (float[]) linkedHashMap3.get(Integer.valueOf(iIntValue5));
                if (fArr3 != null) {
                    updateProfile(iIntValue6, fArr3);
                }
            }
            return linkedHashMap4;
        }
        LinkedHashMap linkedHashMap5 = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(list2, 10)), 16));
        for (Object obj4 : list2) {
            LinkedHashMap linkedHashMap6 = linkedHashMap5;
            int iIntValue7 = ((Number) obj4).intValue();
            int size = this.profiles.size();
            float[] fArr4 = (float[]) linkedHashMap3.get(Integer.valueOf(iIntValue7));
            this.profiles.add(fArr4 == null ? new VoiceProfile(new float[0], 0) : new VoiceProfile(fArr4, 1));
            linkedHashMap6.put(obj4, Integer.valueOf(size));
        }
        return linkedHashMap5;
    }

    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000)\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000*\u0001\u0000\b\u008a\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0006HÆ\u0003J,\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001¢\u0006\u0002\u0010\u0011J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0018"}, d2 = {"com/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$matchSpeakers$Candidate", "", "local", "", "global", "score", "", "(IIF)V", "getGlobal", "()I", "getLocal", "getScore", "()F", "component1", "component2", "component3", "copy", "(IIF)Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$matchSpeakers$Candidate;", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    public static final /* data */ class Candidate {
        private final int global;
        private final int local;
        private final float score;

        public static /* synthetic */ Candidate copy$default(Candidate candidate, int i, int i2, float f, int i3, Object obj) {
            if ((i3 & 1) != 0) {
                i = candidate.local;
            }
            if ((i3 & 2) != 0) {
                i2 = candidate.global;
            }
            if ((i3 & 4) != 0) {
                f = candidate.score;
            }
            return candidate.copy(i, i2, f);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final int getLocal() {
            return this.local;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final int getGlobal() {
            return this.global;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final float getScore() {
            return this.score;
        }

        public final Candidate copy(int local, int global, float score) {
            return new Candidate(local, global, score);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Candidate)) {
                return false;
            }
            Candidate candidate = (Candidate) other;
            return this.local == candidate.local && this.global == candidate.global && Float.compare(this.score, candidate.score) == 0;
        }

        public int hashCode() {
            return (((this.local * 31) + this.global) * 31) + Float.floatToIntBits(this.score);
        }

        public String toString() {
            return "Candidate(local=" + this.local + ", global=" + this.global + ", score=" + this.score + ')';
        }

        public Candidate(int i, int i2, float f) {
            this.local = i;
            this.global = i2;
            this.score = f;
        }

        public final int getGlobal() {
            return this.global;
        }

        public final int getLocal() {
            return this.local;
        }

        public final float getScore() {
            return this.score;
        }
    }

    private final float[] voiceEmbedding(float[] samples, List<Segment> segments) {
        ArrayList<IntRange> arrayList = new ArrayList();
        int last = 0;
        int i = 0;
        for (Segment segment : CollectionsKt.sortedWith(segments, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.LowMemoryTranscriptionEngine$voiceEmbedding$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                LowMemoryTranscriptionEngine.Segment segment2 = (LowMemoryTranscriptionEngine.Segment) t2;
                LowMemoryTranscriptionEngine.Segment segment3 = (LowMemoryTranscriptionEngine.Segment) t;
                return ComparisonsKt.compareValues(Double.valueOf(segment2.getEnd() - segment2.getStart()), Double.valueOf(segment3.getEnd() - segment3.getStart()));
            }
        })) {
            if (i >= 192000) {
                break;
            }
            double d = segment.getEnd() - segment.getStart() > 1.0d ? 0.1d : 0.0d;
            double d2 = 16000;
            int iCoerceIn = RangesKt.coerceIn((int) ((segment.getStart() + d) * d2), 0, samples.length);
            int iMin = Math.min(RangesKt.coerceIn((int) ((segment.getEnd() - d) * d2), iCoerceIn, samples.length), (192000 + iCoerceIn) - i);
            if (iMin > iCoerceIn) {
                arrayList.add(RangesKt.until(iCoerceIn, iMin));
                i += iMin - iCoerceIn;
            }
        }
        if (i < 16000) {
            return null;
        }
        float[] fArr = new float[i];
        for (IntRange intRange : arrayList) {
            ArraysKt.copyInto(samples, fArr, last, intRange.getFirst(), intRange.getLast() + 1);
            last += (intRange.getLast() - intRange.getFirst()) + 1;
        }
        SpeakerEmbeddingExtractor speakerEmbeddingExtractor = this.extractor;
        if (speakerEmbeddingExtractor == null) {
            throw new IllegalArgumentException("Required value was null.".toString());
        }
        OnlineStream onlineStreamCreateStream = speakerEmbeddingExtractor.createStream();
        try {
            onlineStreamCreateStream.acceptWaveform(fArr, 16000);
            onlineStreamCreateStream.inputFinished();
            return speakerEmbeddingExtractor.isReady(onlineStreamCreateStream) ? normalize(speakerEmbeddingExtractor.compute(onlineStreamCreateStream)) : null;
        } finally {
            onlineStreamCreateStream.release();
        }
    }

    private final void updateProfile(int id, float[] embedding) {
        VoiceProfile voiceProfile = this.profiles.get(id);
        if (voiceProfile.getEmbedding().length == 0 || voiceProfile.getWeight() == 0) {
            float[] fArrCopyOf = Arrays.copyOf(embedding, embedding.length);
            Intrinsics.checkNotNullExpressionValue(fArrCopyOf, "copyOf(this, size)");
            voiceProfile.setEmbedding(fArrCopyOf);
            voiceProfile.setWeight(1);
            return;
        }
        int iMin = Math.min(8, voiceProfile.getWeight() + 1);
        int i = iMin - 1;
        Iterator<Integer> it = ArraysKt.getIndices(voiceProfile.getEmbedding()).iterator();
        while (it.hasNext()) {
            int iNextInt = ((IntIterator) it).nextInt();
            voiceProfile.getEmbedding()[iNextInt] = ((voiceProfile.getEmbedding()[iNextInt] * i) + embedding[iNextInt]) / iMin;
        }
        voiceProfile.setEmbedding(normalize(voiceProfile.getEmbedding()));
        voiceProfile.setWeight(iMin);
    }

    private final List<Turn> buildTurns(List<Segment> segments, Map<Integer, Integer> speakerMap, double duration) {
        ArrayList<Turn> arrayList = new ArrayList();
        for (Segment segment : segments) {
            double dCoerceIn = RangesKt.coerceIn(segment.getStart(), 0.0d, duration);
            double dCoerceIn2 = RangesKt.coerceIn(segment.getEnd(), dCoerceIn, duration);
            if (dCoerceIn2 - dCoerceIn >= 0.35d) {
                Integer num = speakerMap.get(Integer.valueOf(segment.getSpeaker()));
                int iIntValue = num != null ? num.intValue() : segment.getSpeaker();
                Turn turn = (Turn) CollectionsKt.lastOrNull((List) arrayList);
                if (turn != null && turn.getSpeaker() == iIntValue && dCoerceIn - turn.getEnd() <= 0.65d && dCoerceIn2 - turn.getStart() <= MAX_TURN_SECONDS) {
                    turn.setEnd(Math.max(turn.getEnd(), dCoerceIn2));
                } else {
                    arrayList.add(new Turn(dCoerceIn, dCoerceIn2, iIntValue));
                }
            }
        }
        ArrayList arrayList2 = new ArrayList();
        for (Turn turn2 : arrayList) {
            double start = turn2.getStart();
            while (turn2.getEnd() - start > MAX_TURN_SECONDS) {
                double d = start + MAX_TURN_SECONDS;
                arrayList2.add(new Turn(start, d, turn2.getSpeaker()));
                start = d;
            }
            if (turn2.getEnd() - start >= 0.35d) {
                arrayList2.add(new Turn(start, turn2.getEnd(), turn2.getSpeaker()));
            }
        }
        return arrayList2;
    }

    private final float[] readPcm16(File file, long firstSample, int count) throws IOException {
        float[] fArr = new float[count];
        byte[] bArr = new byte[131072];
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        try {
            RandomAccessFile randomAccessFile2 = randomAccessFile;
            randomAccessFile2.seek(firstSample * ((long) 2));
            int i = 0;
            while (i < count) {
                int iMin = Math.min(65536, count - i);
                randomAccessFile2.readFully(bArr, 0, iMin * 2);
                int i2 = 0;
                int i3 = 0;
                while (i2 < iMin) {
                    fArr[i] = ((short) ((bArr[i3] & UByte.MAX_VALUE) | (bArr[i3 + 1] << 8))) / 32768.0f;
                    i3 += 2;
                    i2++;
                    i++;
                }
            }
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(randomAccessFile, null);
            return fArr;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(randomAccessFile, th);
                throw th2;
            }
        }
    }

    private final float cosine(float[] a, float[] b) {
        if (a.length == 0 || a.length != b.length) {
            return -1.0f;
        }
        Iterator<Integer> it = ArraysKt.getIndices(a).iterator();
        double d = 0.0d;
        double d2 = 0.0d;
        double d3 = 0.0d;
        while (it.hasNext()) {
            int iNextInt = ((IntIterator) it).nextInt();
            float f = a[iNextInt];
            float f2 = b[iNextInt];
            d2 += (double) (f * f2);
            d += (double) (f * f);
            d3 += (double) (f2 * f2);
        }
        if (d == 0.0d || d3 == 0.0d) {
            return -1.0f;
        }
        return (float) (d2 / Math.sqrt(d * d3));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int progress(int base, int width, double fraction) {
        return RangesKt.coerceIn((int) (((double) base) + (((double) width) * RangesKt.coerceIn(fraction, 0.0d, 1.0d))), 0, 99);
    }

    private final void checkCancelled(AtomicBoolean cancelled) {
        if (cancelled.get()) {
            throw new CancellationException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0006HÆ\u0003J'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0017"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$Segment;", "", "start", "", "end", "speaker", "", "(DDI)V", "getEnd", "()D", "getSpeaker", "()I", "getStart", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    static final /* data */ class Segment {
        private final double end;
        private final int speaker;
        private final double start;

        public static /* synthetic */ Segment copy$default(Segment segment, double d, double d2, int i, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                d = segment.start;
            }
            double d3 = d;
            if ((i2 & 2) != 0) {
                d2 = segment.end;
            }
            double d4 = d2;
            if ((i2 & 4) != 0) {
                i = segment.speaker;
            }
            return segment.copy(d3, d4, i);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final double getStart() {
            return this.start;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final double getEnd() {
            return this.end;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final int getSpeaker() {
            return this.speaker;
        }

        public final Segment copy(double start, double end, int speaker) {
            return new Segment(start, end, speaker);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Segment)) {
                return false;
            }
            Segment segment = (Segment) other;
            return Double.compare(this.start, segment.start) == 0 && Double.compare(this.end, segment.end) == 0 && this.speaker == segment.speaker;
        }

        public int hashCode() {
            return (((DecodedAudio$$ExternalSyntheticBackport0.m(this.start) * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.end)) * 31) + this.speaker;
        }

        public String toString() {
            return "Segment(start=" + this.start + ", end=" + this.end + ", speaker=" + this.speaker + ')';
        }

        public Segment(double d, double d2, int i) {
            this.start = d;
            this.end = d2;
            this.speaker = i;
        }

        public final double getEnd() {
            return this.end;
        }

        public final int getSpeaker() {
            return this.speaker;
        }

        public final double getStart() {
            return this.start;
        }
    }

    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0002\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J-\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$ChunkPlan;", "", "firstSample", "", "sampleCount", "", "segments", "", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$Segment;", "(JILjava/util/List;)V", "getFirstSample", "()J", "getSampleCount", "()I", "getSegments", "()Ljava/util/List;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    private static final /* data */ class ChunkPlan {
        private final long firstSample;
        private final int sampleCount;
        private final List<Segment> segments;

        /* JADX WARN: Multi-variable type inference failed */
        public static /* synthetic */ ChunkPlan copy$default(ChunkPlan chunkPlan, long j, int i, List list, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                j = chunkPlan.firstSample;
            }
            if ((i2 & 2) != 0) {
                i = chunkPlan.sampleCount;
            }
            if ((i2 & 4) != 0) {
                list = chunkPlan.segments;
            }
            return chunkPlan.copy(j, i, list);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final long getFirstSample() {
            return this.firstSample;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final int getSampleCount() {
            return this.sampleCount;
        }

        public final List<Segment> component3() {
            return this.segments;
        }

        public final ChunkPlan copy(long firstSample, int sampleCount, List<Segment> segments) {
            Intrinsics.checkNotNullParameter(segments, "segments");
            return new ChunkPlan(firstSample, sampleCount, segments);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ChunkPlan)) {
                return false;
            }
            ChunkPlan chunkPlan = (ChunkPlan) other;
            return this.firstSample == chunkPlan.firstSample && this.sampleCount == chunkPlan.sampleCount && Intrinsics.areEqual(this.segments, chunkPlan.segments);
        }

        public int hashCode() {
            return (((DecodedAudio$$ExternalSyntheticBackport0.m(this.firstSample) * 31) + this.sampleCount) * 31) + this.segments.hashCode();
        }

        public String toString() {
            return "ChunkPlan(firstSample=" + this.firstSample + ", sampleCount=" + this.sampleCount + ", segments=" + this.segments + ')';
        }

        public ChunkPlan(long j, int i, List<Segment> segments) {
            Intrinsics.checkNotNullParameter(segments, "segments");
            this.firstSample = j;
            this.sampleCount = i;
            this.segments = segments;
        }

        public final long getFirstSample() {
            return this.firstSample;
        }

        public final int getSampleCount() {
            return this.sampleCount;
        }

        public final List<Segment> getSegments() {
            return this.segments;
        }
    }

    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0018"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$VoiceProfile;", "", "embedding", "", "weight", "", "([FI)V", "getEmbedding", "()[F", "setEmbedding", "([F)V", "getWeight", "()I", "setWeight", "(I)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    private static final /* data */ class VoiceProfile {
        private float[] embedding;
        private int weight;

        public static /* synthetic */ VoiceProfile copy$default(VoiceProfile voiceProfile, float[] fArr, int i, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                fArr = voiceProfile.embedding;
            }
            if ((i2 & 2) != 0) {
                i = voiceProfile.weight;
            }
            return voiceProfile.copy(fArr, i);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final float[] getEmbedding() {
            return this.embedding;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final int getWeight() {
            return this.weight;
        }

        public final VoiceProfile copy(float[] embedding, int weight) {
            Intrinsics.checkNotNullParameter(embedding, "embedding");
            return new VoiceProfile(embedding, weight);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof VoiceProfile)) {
                return false;
            }
            VoiceProfile voiceProfile = (VoiceProfile) other;
            return Intrinsics.areEqual(this.embedding, voiceProfile.embedding) && this.weight == voiceProfile.weight;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.embedding) * 31) + this.weight;
        }

        public String toString() {
            return "VoiceProfile(embedding=" + Arrays.toString(this.embedding) + ", weight=" + this.weight + ')';
        }

        public VoiceProfile(float[] embedding, int i) {
            Intrinsics.checkNotNullParameter(embedding, "embedding");
            this.embedding = embedding;
            this.weight = i;
        }

        public final float[] getEmbedding() {
            return this.embedding;
        }

        public final int getWeight() {
            return this.weight;
        }

        public final void setEmbedding(float[] fArr) {
            Intrinsics.checkNotNullParameter(fArr, "<set-?>");
            this.embedding = fArr;
        }

        public final void setWeight(int i) {
            this.weight = i;
        }
    }

    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\t\"\u0004\b\u000f\u0010\u000b¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$Turn;", "", "start", "", "end", "speaker", "", "(DDI)V", "getEnd", "()D", "setEnd", "(D)V", "getSpeaker", "()I", "getStart", "setStart", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    private static final /* data */ class Turn {
        private double end;
        private final int speaker;
        private double start;

        public static /* synthetic */ Turn copy$default(Turn turn, double d, double d2, int i, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                d = turn.start;
            }
            double d3 = d;
            if ((i2 & 2) != 0) {
                d2 = turn.end;
            }
            double d4 = d2;
            if ((i2 & 4) != 0) {
                i = turn.speaker;
            }
            return turn.copy(d3, d4, i);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final double getStart() {
            return this.start;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final double getEnd() {
            return this.end;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final int getSpeaker() {
            return this.speaker;
        }

        public final Turn copy(double start, double end, int speaker) {
            return new Turn(start, end, speaker);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Turn)) {
                return false;
            }
            Turn turn = (Turn) other;
            return Double.compare(this.start, turn.start) == 0 && Double.compare(this.end, turn.end) == 0 && this.speaker == turn.speaker;
        }

        public int hashCode() {
            return (((DecodedAudio$$ExternalSyntheticBackport0.m(this.start) * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.end)) * 31) + this.speaker;
        }

        public String toString() {
            return "Turn(start=" + this.start + ", end=" + this.end + ", speaker=" + this.speaker + ')';
        }

        public Turn(double d, double d2, int i) {
            this.start = d;
            this.end = d2;
            this.speaker = i;
        }

        public final double getEnd() {
            return this.end;
        }

        public final int getSpeaker() {
            return this.speaker;
        }

        public final double getStart() {
            return this.start;
        }

        public final void setEnd(double d) {
            this.end = d;
        }

        public final void setStart(double d) {
            this.start = d;
        }
    }

    /* JADX INFO: compiled from: LowMemoryTranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0005HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t¨\u0006\u0017"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/LowMemoryTranscriptionEngine$PendingTurn;", "", "speaker", "", "start", "", "end", "(IDD)V", "getEnd", "()D", "getSpeaker", "()I", "getStart", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    private static final /* data */ class PendingTurn {
        private final double end;
        private final int speaker;
        private final double start;

        public static /* synthetic */ PendingTurn copy$default(PendingTurn pendingTurn, int i, double d, double d2, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = pendingTurn.speaker;
            }
            if ((i2 & 2) != 0) {
                d = pendingTurn.start;
            }
            double d3 = d;
            if ((i2 & 4) != 0) {
                d2 = pendingTurn.end;
            }
            return pendingTurn.copy(i, d3, d2);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final int getSpeaker() {
            return this.speaker;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final double getStart() {
            return this.start;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final double getEnd() {
            return this.end;
        }

        public final PendingTurn copy(int speaker, double start, double end) {
            return new PendingTurn(speaker, start, end);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PendingTurn)) {
                return false;
            }
            PendingTurn pendingTurn = (PendingTurn) other;
            return this.speaker == pendingTurn.speaker && Double.compare(this.start, pendingTurn.start) == 0 && Double.compare(this.end, pendingTurn.end) == 0;
        }

        public int hashCode() {
            return (((this.speaker * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.start)) * 31) + DecodedAudio$$ExternalSyntheticBackport0.m(this.end);
        }

        public String toString() {
            return "PendingTurn(speaker=" + this.speaker + ", start=" + this.start + ", end=" + this.end + ')';
        }

        public PendingTurn(int i, double d, double d2) {
            this.speaker = i;
            this.start = d;
            this.end = d2;
        }

        public final double getEnd() {
            return this.end;
        }

        public final int getSpeaker() {
            return this.speaker;
        }

        public final double getStart() {
            return this.start;
        }
    }

    private final float[] normalize(float[] values) {
        double d = 0.0d;
        for (float f : values) {
            d += (double) (f * f);
        }
        float fSqrt = (float) Math.sqrt(d);
        if (fSqrt > 0.0f) {
            Iterator<Integer> it = ArraysKt.getIndices(values).iterator();
            while (it.hasNext()) {
                int iNextInt = ((IntIterator) it).nextInt();
                values[iNextInt] = values[iNextInt] / fSqrt;
            }
        }
        return values;
    }
}
