package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.content.res.AssetManager;
import com.k2fsa.sherpa.onnx.FastClusteringConfig;
import com.k2fsa.sherpa.onnx.FeatureConfig;
import com.k2fsa.sherpa.onnx.OfflineModelConfig;
import com.k2fsa.sherpa.onnx.OfflineRecognizer;
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarization;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationSegment;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationModelConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationPyannoteModelConfig;
import com.k2fsa.sherpa.onnx.OfflineStream;
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig;
import com.k2fsa.sherpa.onnx.OnlineStream;
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractor;
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractorConfig;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: TranscriptionEngine.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 =2\u00020\u0001:\u0003=>?B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J8\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0002J\u0010\u0010!\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020\u0015H\u0002J2\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010$\u001a\u00020\u001f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f2\u0006\u0010\"\u001a\u00020\u0015H\u0002J\u001c\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u000f2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020&0\u000fH\u0002J\u0010\u0010(\u001a\u00020\u001f2\u0006\u0010)\u001a\u00020\u001fH\u0002J \u0010*\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\u0015H\u0002J\u0010\u00100\u001a\u0002012\u0006\u00102\u001a\u00020\u001fH\u0002J\u0006\u00103\u001a\u00020\u0019J>\u00104\u001a\b\u0012\u0004\u0012\u00020&0\u000f2\u0006\u00105\u001a\u0002062\u0006\u0010\"\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u001b2\u0018\u00107\u001a\u0014\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u000201\u0012\u0004\u0012\u00020\u001908J\u0018\u00109\u001a\u00020\u00192\u0006\u0010:\u001a\u00020\u00152\u0006\u0010;\u001a\u00020\u001fH\u0002J \u0010<\u001a\u0004\u0018\u00010\u001f2\u0006\u0010$\u001a\u00020\u001f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006@"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine;", "", "assets", "Landroid/content/res/AssetManager;", "(Landroid/content/res/AssetManager;)V", "diarizer", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarization;", "extractor", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;", "profiles", "", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$VoiceProfile;", "recognizer", "Lcom/k2fsa/sherpa/onnx/OfflineRecognizer;", "buildTurns", "", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$Turn;", "segments", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "speakerMap", "", "", "duration", "", "checkCancelled", "", "cancelled", "Ljava/util/concurrent/atomic/AtomicBoolean;", "cosine", "", "a", "", "b", "initialize", "requestedSpeakers", "matchSpeakers", "samples", "mergeLines", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptLine;", "lines", "normalize", "values", "readPcm16", "file", "Ljava/io/File;", "firstSample", "", "count", "recognize", "", "waveform", "release", "transcribe", "audio", "Lcom/k2fsa/sherpa/onnx/speaker/diarization/DecodedAudio;", "onProgress", "Lkotlin/Function2;", "updateProfile", "id", "embedding", "voiceEmbedding", "Companion", "Turn", "VoiceProfile", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
public final class TranscriptionEngine {
    private static final int CHUNK_SECONDS = 720;
    private static final double MAX_TURN_SECONDS = 28.0d;
    private static final double PROFILE_SECONDS = 12.0d;
    private static final int SAMPLE_RATE = 16000;
    private final AssetManager assets;
    private OfflineSpeakerDiarization diarizer;
    private SpeakerEmbeddingExtractor extractor;
    private final List<VoiceProfile> profiles;
    private OfflineRecognizer recognizer;

    public TranscriptionEngine(AssetManager assets) {
        Intrinsics.checkNotNullParameter(assets, "assets");
        this.assets = assets;
        this.profiles = new ArrayList();
    }

    public final List<TranscriptLine> transcribe(DecodedAudio audio, int requestedSpeakers, final AtomicBoolean cancelled, final Function2<? super Integer, ? super String, Unit> onProgress) throws IOException {
        ArrayList arrayList;
        int i = requestedSpeakers;
        Intrinsics.checkNotNullParameter(audio, "audio");
        Intrinsics.checkNotNullParameter(cancelled, "cancelled");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        checkCancelled(cancelled);
        onProgress.invoke(20, "Caricamento dei modelli offline…");
        initialize(i);
        this.profiles.clear();
        long j = 11520000;
        int i2 = 1;
        int iCoerceAtLeast = RangesKt.coerceAtLeast((int) Math.ceil(audio.getSampleCount() / 11520000), 1);
        ArrayList arrayList2 = new ArrayList();
        int i3 = 0;
        while (i3 < iCoerceAtLeast) {
            checkCancelled(cancelled);
            long j2 = ((long) i3) * j;
            float[] pcm16 = readPcm16(audio.getPcmFile(), j2, (int) Math.min(j, audio.getSampleCount() - j2));
            double length = pcm16.length;
            double d = 16000;
            double d2 = length / d;
            double d3 = j2 / d;
            final double d4 = 74.0d / ((double) iCoerceAtLeast);
            final double d5 = 25.0d + (((double) i3) * d4);
            String str = iCoerceAtLeast > i2 ? " " + (i3 + 1) + '/' + iCoerceAtLeast : "";
            onProgress.invoke(Integer.valueOf((int) d5), "Distinzione delle voci" + str + Typography.ellipsis);
            OfflineSpeakerDiarization offlineSpeakerDiarization = this.diarizer;
            if (offlineSpeakerDiarization == null) {
                throw new IllegalArgumentException("Required value was null.".toString());
            }
            offlineSpeakerDiarization.getConfig().getClustering().setNumClusters(i > 0 ? i : -1);
            offlineSpeakerDiarization.setConfig(offlineSpeakerDiarization.getConfig());
            final String str2 = str;
            int i4 = i3;
            boolean z = false;
            ArrayList arrayList3 = arrayList2;
            int i5 = iCoerceAtLeast;
            OfflineSpeakerDiarizationSegment[] offlineSpeakerDiarizationSegmentArrProcessWithCallback$default = OfflineSpeakerDiarization.processWithCallback$default(offlineSpeakerDiarization, pcm16, new Function3<Integer, Integer, Long, Integer>() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.TranscriptionEngine$transcribe$segments$1
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
                        double d6 = d5 + (d4 * 0.35d * (i7 > 0 ? ((double) i6) / ((double) i7) : 0.0d));
                        onProgress.invoke(Integer.valueOf(RangesKt.coerceIn((int) d6, 25, 98)), "Distinzione delle voci" + str2 + Typography.ellipsis);
                        i8 = 0;
                    }
                    return Integer.valueOf(i8);
                }
            }, 0L, 4, null);
            ArrayList arrayList4 = new ArrayList();
            for (OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment : offlineSpeakerDiarizationSegmentArrProcessWithCallback$default) {
                if (offlineSpeakerDiarizationSegment.getEnd() > offlineSpeakerDiarizationSegment.getStart() && offlineSpeakerDiarizationSegment.getEnd() > 0.0f) {
                    arrayList4.add(offlineSpeakerDiarizationSegment);
                }
            }
            List<OfflineSpeakerDiarizationSegment> listSortedWith = CollectionsKt.sortedWith(arrayList4, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.TranscriptionEngine$transcribe$$inlined$sortedBy$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.Comparator
                public final int compare(T t, T t2) {
                    return ComparisonsKt.compareValues(Float.valueOf(((OfflineSpeakerDiarizationSegment) t).getStart()), Float.valueOf(((OfflineSpeakerDiarizationSegment) t2).getStart()));
                }
            });
            checkCancelled(cancelled);
            if (!listSortedWith.isEmpty()) {
                List<Turn> listBuildTurns = buildTurns(listSortedWith, matchSpeakers(pcm16, listSortedWith, i), d2);
                int i6 = 0;
                for (Object obj : listBuildTurns) {
                    int i7 = i6 + 1;
                    if (i6 < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    Turn turn = (Turn) obj;
                    checkCancelled(cancelled);
                    double d6 = d2;
                    Integer numValueOf = Integer.valueOf(RangesKt.coerceIn((int) (d5 + ((((((double) i6) / ((double) RangesKt.coerceAtLeast(listBuildTurns.size(), 1))) * 0.64d) + 0.35d) * d4)), 25, 98));
                    StringBuilder sb = new StringBuilder("Trascrizione");
                    String str3 = str2;
                    sb.append(str3);
                    sb.append(" — Persona ");
                    sb.append(turn.getSpeaker() + 1);
                    sb.append(Typography.ellipsis);
                    onProgress.invoke(numValueOf, sb.toString());
                    List<Turn> list = listBuildTurns;
                    double dMax = Math.max(0.0d, turn.getStart() - 0.1d);
                    double dMin = Math.min(d6, turn.getEnd() + 0.1d);
                    int iCoerceIn = RangesKt.coerceIn((int) (dMax * d), 0, pcm16.length);
                    int iCoerceIn2 = RangesKt.coerceIn((int) (dMin * d), iCoerceIn, pcm16.length);
                    if (iCoerceIn2 - iCoerceIn >= 7200) {
                        String strRecognize = recognize(ArraysKt.copyOfRange(pcm16, iCoerceIn, iCoerceIn2));
                        if (!StringsKt.isBlank(strRecognize)) {
                            arrayList = arrayList3;
                            arrayList.add(new TranscriptLine(turn.getSpeaker() + 1, d3 + turn.getStart(), d3 + turn.getEnd(), strRecognize));
                        } else {
                            arrayList = arrayList3;
                        }
                    } else {
                        arrayList = arrayList3;
                    }
                    arrayList3 = arrayList;
                    d2 = d6;
                    i6 = i7;
                    str2 = str3;
                    z = false;
                    listBuildTurns = list;
                }
            }
            i3 = i4 + 1;
            arrayList2 = arrayList3;
            i2 = 1;
            iCoerceAtLeast = i5;
            j = 11520000;
            i = requestedSpeakers;
        }
        checkCancelled(cancelled);
        onProgress.invoke(99, "Preparazione del file TXT…");
        return mergeLines(arrayList2);
    }

    public final void release() {
        OfflineRecognizer offlineRecognizer = this.recognizer;
        if (offlineRecognizer != null) {
            offlineRecognizer.release();
        }
        this.recognizer = null;
        OfflineSpeakerDiarization offlineSpeakerDiarization = this.diarizer;
        if (offlineSpeakerDiarization != null) {
            offlineSpeakerDiarization.release();
        }
        this.diarizer = null;
        SpeakerEmbeddingExtractor speakerEmbeddingExtractor = this.extractor;
        if (speakerEmbeddingExtractor != null) {
            speakerEmbeddingExtractor.release();
        }
        this.extractor = null;
        this.profiles.clear();
    }

    private final void initialize(int requestedSpeakers) {
        TranscriptionEngine transcriptionEngine = this;
        if (transcriptionEngine.recognizer == null) {
            OfflineRecognizer offlineRecognizer = new OfflineRecognizer(transcriptionEngine.assets, new OfflineRecognizerConfig(new FeatureConfig(16000, 80, 0.0f, 4, null), new OfflineModelConfig(null, null, new OfflineWhisperModelConfig("sherpa-onnx-whisper-tiny/tiny-encoder.int8.onnx", "sherpa-onnx-whisper-tiny/tiny-decoder.int8.onnx", "it", "transcribe", 1000, false, false, 96, null), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 4, false, "cpu", "whisper", "sherpa-onnx-whisper-tiny/tiny-tokens.txt", null, null, 25427963, null), null, "greedy_search", 0, null, 0.0f, null, null, 0.0f, 1012, null));
            transcriptionEngine = this;
            transcriptionEngine.recognizer = offlineRecognizer;
        }
        if (transcriptionEngine.diarizer == null) {
            transcriptionEngine.diarizer = new OfflineSpeakerDiarization(transcriptionEngine.assets, new OfflineSpeakerDiarizationConfig(new OfflineSpeakerSegmentationModelConfig(new OfflineSpeakerSegmentationPyannoteModelConfig("segmentation.onnx", 0.1f), 2, false, "cpu"), new SpeakerEmbeddingExtractorConfig("embedding.onnx", 2, false, "cpu"), new FastClusteringConfig(requestedSpeakers > 0 ? requestedSpeakers : -1, 0.5f), 0.2f, 0.5f));
        }
        if (transcriptionEngine.extractor == null) {
            transcriptionEngine.extractor = new SpeakerEmbeddingExtractor(transcriptionEngine.assets, new SpeakerEmbeddingExtractorConfig("embedding.onnx", 2, false, "cpu"));
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
    private final Map<Integer, Integer> matchSpeakers(float[] samples, List<OfflineSpeakerDiarizationSegment> segments, int requestedSpeakers) {
        Integer next;
        Integer next2;
        int iIntValue;
        List<OfflineSpeakerDiarizationSegment> list = segments;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(((OfflineSpeakerDiarizationSegment) it.next()).getSpeaker()));
        }
        List listDistinct = CollectionsKt.distinct(arrayList);
        List list2 = listDistinct;
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(list2, 10)), 16));
        for (Object obj : list2) {
            LinkedHashMap linkedHashMap2 = linkedHashMap;
            int iIntValue2 = ((Number) obj).intValue();
            ArrayList arrayList2 = new ArrayList();
            for (Object obj2 : list) {
                if (((OfflineSpeakerDiarizationSegment) obj2).getSpeaker() == iIntValue2) {
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
            for (Candidate candidate : CollectionsKt.sortedWith(arrayList3, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.TranscriptionEngine$matchSpeakers$$inlined$sortedByDescending$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.Comparator
                public final int compare(T t, T t2) {
                    return ComparisonsKt.compareValues(Float.valueOf(((TranscriptionEngine.Candidate) t2).getScore()), Float.valueOf(((TranscriptionEngine.Candidate) t).getScore()));
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

    /* JADX INFO: compiled from: TranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000)\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000*\u0001\u0000\b\u008a\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0006HÆ\u0003J,\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001¢\u0006\u0002\u0010\u0011J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0018"}, d2 = {"com/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$matchSpeakers$Candidate", "", "local", "", "global", "score", "", "(IIF)V", "getGlobal", "()I", "getLocal", "getScore", "()F", "component1", "component2", "component3", "copy", "(IIF)Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$matchSpeakers$Candidate;", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
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

    private final float[] voiceEmbedding(float[] samples, List<OfflineSpeakerDiarizationSegment> segments) {
        ArrayList<IntRange> arrayList = new ArrayList();
        int last = 0;
        int i = 0;
        for (OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment : CollectionsKt.sortedWith(segments, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.TranscriptionEngine$voiceEmbedding$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment2 = (OfflineSpeakerDiarizationSegment) t2;
                OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment3 = (OfflineSpeakerDiarizationSegment) t;
                return ComparisonsKt.compareValues(Float.valueOf(offlineSpeakerDiarizationSegment2.getEnd() - offlineSpeakerDiarizationSegment2.getStart()), Float.valueOf(offlineSpeakerDiarizationSegment3.getEnd() - offlineSpeakerDiarizationSegment3.getStart()));
            }
        })) {
            if (i >= 192000) {
                break;
            }
            float f = offlineSpeakerDiarizationSegment.getEnd() - offlineSpeakerDiarizationSegment.getStart() > 1.0f ? 0.1f : 0.0f;
            float f2 = 16000;
            int iCoerceIn = RangesKt.coerceIn((int) ((offlineSpeakerDiarizationSegment.getStart() + f) * f2), 0, samples.length);
            int iMin = Math.min(RangesKt.coerceIn((int) ((offlineSpeakerDiarizationSegment.getEnd() - f) * f2), iCoerceIn, samples.length), (192000 + iCoerceIn) - i);
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

    private final List<Turn> buildTurns(List<OfflineSpeakerDiarizationSegment> segments, Map<Integer, Integer> speakerMap, double duration) {
        ArrayList<Turn> arrayList = new ArrayList();
        for (OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment : segments) {
            double dCoerceIn = RangesKt.coerceIn(offlineSpeakerDiarizationSegment.getStart(), 0.0d, duration);
            double dCoerceIn2 = RangesKt.coerceIn(offlineSpeakerDiarizationSegment.getEnd(), dCoerceIn, duration);
            if (dCoerceIn2 - dCoerceIn >= 0.35d) {
                Integer num = speakerMap.get(Integer.valueOf(offlineSpeakerDiarizationSegment.getSpeaker()));
                int iIntValue = num != null ? num.intValue() : offlineSpeakerDiarizationSegment.getSpeaker();
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

    private final List<TranscriptLine> mergeLines(List<TranscriptLine> lines) {
        ArrayList arrayList = new ArrayList();
        for (TranscriptLine transcriptLine : CollectionsKt.sortedWith(lines, new Comparator() { // from class: com.k2fsa.sherpa.onnx.speaker.diarization.TranscriptionEngine$mergeLines$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                return ComparisonsKt.compareValues(Double.valueOf(((TranscriptLine) t).getStartSeconds()), Double.valueOf(((TranscriptLine) t2).getStartSeconds()));
            }
        })) {
            TranscriptLine transcriptLine2 = (TranscriptLine) CollectionsKt.lastOrNull((List) arrayList);
            if (transcriptLine2 != null && transcriptLine2.getSpeaker() == transcriptLine.getSpeaker() && transcriptLine.getStartSeconds() - transcriptLine2.getEndSeconds() <= 0.8d && transcriptLine2.getText().length() + transcriptLine.getText().length() < 600) {
                arrayList.set(CollectionsKt.getLastIndex(arrayList), TranscriptLine.copy$default(transcriptLine2, 0, 0.0d, transcriptLine.getEndSeconds(), StringsKt.trim((CharSequence) (transcriptLine2.getText() + ' ' + transcriptLine.getText())).toString(), 3, null));
            } else {
                arrayList.add(transcriptLine);
            }
        }
        return arrayList;
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

    private final void checkCancelled(AtomicBoolean cancelled) {
        if (cancelled.get()) {
            throw new CancellationException();
        }
    }

    /* JADX INFO: compiled from: TranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0018"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$VoiceProfile;", "", "embedding", "", "weight", "", "([FI)V", "getEmbedding", "()[F", "setEmbedding", "([F)V", "getWeight", "()I", "setWeight", "(I)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
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

    /* JADX INFO: compiled from: TranscriptionEngine.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\t\"\u0004\b\u000f\u0010\u000b¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/speaker/diarization/TranscriptionEngine$Turn;", "", "start", "", "end", "speaker", "", "(DDI)V", "getEnd", "()D", "setEnd", "(D)V", "getSpeaker", "()I", "getStart", "setStart", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
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
