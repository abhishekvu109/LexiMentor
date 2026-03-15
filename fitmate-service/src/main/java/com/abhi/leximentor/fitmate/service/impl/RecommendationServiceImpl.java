package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.rec.ExerciseRecommendationDTO;
import com.abhi.leximentor.fitmate.dto.rec.RecommendationDTO;
import com.abhi.leximentor.fitmate.dto.rec.RecommendationRequestDTO;
import com.abhi.leximentor.fitmate.dto.rec.ml.*;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Training;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.ExerciseMapper;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.repository.DrillRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.TrainingRepository;
import com.abhi.leximentor.fitmate.service.MlServiceClient;
import com.abhi.leximentor.fitmate.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hybrid recommendation engine: tries the Python ML microservice first,
 * falls back to the built-in rule engine when the ML service is unavailable.
 *
 * <h3>Two-step ML pipeline (when models are trained)</h3>
 * <ol>
 *   <li><b>Ranking</b> — {@code POST /predict/exercises}: ML service re-ranks
 *       body-part-filtered candidates by predicted user preference.</li>
 *   <li><b>Performance</b> — {@code POST /predict/performance}: ML service
 *       predicts optimal weight + reps for the top-ranked exercises.</li>
 * </ol>
 * Each step falls back <em>independently</em>: if ranking succeeds but
 * performance prediction fails, the ML-ranked order is kept while
 * progressive overload logic supplies the load targets.
 *
 * <h3>Rule engine (Phase 1 / fallback)</h3>
 * Neglect + frequency scoring → ranked list → progressive overload load targets.
 * Works with zero training data on day one.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendationServiceImpl implements RecommendationService {

    // ── Tuning knobs ─────────────────────────────────────────────────────────

    private static final double NEGLECT_WEIGHT = 0.6;
    private static final double FREQUENCY_WEIGHT = 0.4;
    private static final int TARGET_REPS_MIN = 8;
    private static final int TARGET_REPS_MAX = 12;
    private static final double INCREASE_PCT = 0.025;
    private static final double DELOAD_PCT = 0.15;
    private static final int NEGLECT_CEILING_DAYS = 30;
    private static final int DEFAULT_SETS = 3;

    // ── Dependencies ─────────────────────────────────────────────────────────

    private final BodyPartsRepository bodyPartsRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;
    private final DrillRepository drillRepository;
    private final ExerciseMapper exerciseMapper;
    private final MlServiceClient mlServiceClient;

    // ── Public API ────────────────────────────────────────────────────────────

    @Override
    public RecommendationDTO recommend(RecommendationRequestDTO request) {
        log.info("[Recommendation] user='{}' bodyPart='{}' training='{}'",
                request.getUsername(), request.getTargetBodyPartRefId(), request.getTrainingRefId());

        BodyPart bodyPart = resolveBodyPart(request.getTargetBodyPartRefId());
        Training training = resolveTraining(request.getTrainingRefId());
        int limit = request.getMaxExercises() > 0 ? request.getMaxExercises() : 5;

        // 1. Candidate exercises (filtered by body part ± training type)
        List<Exercise> candidates = fetchCandidates(bodyPart, training);
        log.debug("[Recommendation] {} candidates for body part '{}'", candidates.size(), bodyPart.getName());

        // 2. Drill history per exercise (used by ML feature builders and rule engine)
        Map<Long, List<Drill>> history = loadHistory(request.getUsername(), candidates);

        // 3. RANKING — try ML, fall back to rule engine
        List<Exercise> ranked = mlRanking(request, bodyPart, training, candidates, history)
                .orElseGet(() -> ruleEngineRanking(candidates, history));

        List<Exercise> topN = ranked.stream().limit(limit).toList();

        // 4. PERFORMANCE — try ML (batch), fall back to rule engine per exercise
        Map<Long, MlPerformancePrediction> mlPredictions =
                mlPerformance(request.getUsername(), topN, history, training);

        // 5. Build final response
        String engineVersion = mlPredictions.isEmpty() ? "RULE_ENGINE_V1" : "HYBRID_V1";
        List<ExerciseRecommendationDTO> recommendations = topN.stream()
                .map(ex -> buildRecommendation(
                        ex,
                        history.getOrDefault(ex.getRefId(), List.of()),
                        mlPredictions.get(ex.getRefId())))
                .toList();

        return RecommendationDTO.builder()
                .username(request.getUsername())
                .targetBodyPart(bodyPart.getName())
                .trainingType(training != null ? training.getName() : null)
                .recommendations(recommendations)
                .generatedAt(LocalDateTime.now())
                .engineVersion(engineVersion)
                .build();
    }

    // ── Step 3: Ranking ───────────────────────────────────────────────────────

    /**
     * Ask the ML service to rank the candidates.
     *
     * @return sorted exercises (best first), or {@code Optional.empty()} if ML unavailable
     */
    private Optional<List<Exercise>> mlRanking(RecommendationRequestDTO request,
                                                BodyPart bodyPart,
                                                Training training,
                                                List<Exercise> candidates,
                                                Map<Long, List<Drill>> history) {
        List<MlExerciseFeatures> featureList = candidates.stream()
                .map(ex -> buildExerciseFeatures(ex, history.getOrDefault(ex.getRefId(), List.of())))
                .toList();

        MlExerciseRankerRequest mlRequest = MlExerciseRankerRequest.builder()
                .username(request.getUsername())
                .targetBodyPartRefId(bodyPart.getRefId())
                .trainingRefId(training != null ? training.getRefId() : null)
                .candidates(featureList)
                .build();

        Optional<MlExerciseRankerResponse> mlResponse = mlServiceClient.rankExercises(mlRequest);

        if (mlResponse.isEmpty()) {
            log.debug("[Recommendation] ML ranker unavailable — using rule engine ranking");
            return Optional.empty();
        }

        // Map refId → ML rank, then reorder candidates accordingly
        Map<Long, Integer> rankMap = mlResponse.get().getRankedExercises().stream()
                .collect(Collectors.toMap(MlRankedExercise::getExerciseRefId, MlRankedExercise::getRank));

        List<Exercise> mlSorted = candidates.stream()
                .sorted(Comparator.comparingInt(ex -> rankMap.getOrDefault(ex.getRefId(), Integer.MAX_VALUE)))
                .toList();

        log.info("[Recommendation] ML ranking applied (model={})", mlResponse.get().getModelVersion());
        return Optional.of(mlSorted);
    }

    /** Rule engine ranking: neglect score + frequency score → descending sort. */
    private List<Exercise> ruleEngineRanking(List<Exercise> candidates, Map<Long, List<Drill>> history) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble(
                        (Exercise ex) -> compositeScore(history.getOrDefault(ex.getRefId(), List.of())))
                        .reversed())
                .toList();
    }

    // ── Step 4: Performance prediction ───────────────────────────────────────

    /**
     * Batch-predict performance for the top-N exercises.
     *
     * @return map of exerciseRefId → prediction (empty map if ML unavailable)
     */
    private Map<Long, MlPerformancePrediction> mlPerformance(String username,
                                                              List<Exercise> topN,
                                                              Map<Long, List<Drill>> history,
                                                              Training training) {
        List<MlPerformanceFeatures> featureList = topN.stream()
                .map(ex -> buildPerformanceFeatures(
                        ex, username, history.getOrDefault(ex.getRefId(), List.of()), training))
                .toList();

        Optional<MlPerformancePredictorResponse> mlResponse =
                mlServiceClient.predictPerformance(featureList);

        if (mlResponse.isEmpty()) {
            log.debug("[Recommendation] ML performance predictor unavailable — using progressive overload fallback");
            return Map.of();
        }

        log.info("[Recommendation] ML performance prediction applied (model={})", mlResponse.get().getModelVersion());
        return mlResponse.get().getPredictions().stream()
                .collect(Collectors.toMap(MlPerformancePrediction::getExerciseRefId, p -> p));
    }

    // ── Step 5: Build recommendation DTO ─────────────────────────────────────

    private ExerciseRecommendationDTO buildRecommendation(Exercise exercise,
                                                          List<Drill> history,
                                                          MlPerformancePrediction mlPrediction) {
        int daysSince = computeDaysSinceLastDone(history);

        if (mlPrediction != null) {
            return ExerciseRecommendationDTO.builder()
                    .exercise(exerciseMapper.toDto(exercise))
                    .recommendedWeight(mlPrediction.getRecommendedWeight())
                    .recommendedSets(mlPrediction.getRecommendedSets())
                    .recommendedReps(mlPrediction.getRecommendedReps())
                    .weightUnit(exercise.getUnit())
                    .neglectScore(computeNeglectScore(daysSince))
                    .daysSinceLastDone(daysSince)
                    .progressionNote(mlPrediction.getProgressionNote())
                    .source("ML_MODEL")
                    .build();
        }

        // Rule engine fallback for load targets
        Progression progression = calculateProgression(exercise, history);
        return ExerciseRecommendationDTO.builder()
                .exercise(exerciseMapper.toDto(exercise))
                .recommendedWeight(progression.weight())
                .recommendedSets(progression.sets())
                .recommendedReps(progression.reps())
                .weightUnit(exercise.getUnit())
                .neglectScore(computeNeglectScore(daysSince))
                .daysSinceLastDone(daysSince)
                .progressionNote(progression.note())
                .source("RULE_ENGINE")
                .build();
    }

    // ── Feature builders ──────────────────────────────────────────────────────

    private MlExerciseFeatures buildExerciseFeatures(Exercise exercise, List<Drill> history) {
        int daysSince = computeDaysSinceLastDone(history);
        return MlExerciseFeatures.builder()
                .exerciseRefId(exercise.getRefId())
                .exerciseName(exercise.getName())
                .daysSinceLastDone(daysSince)
                .frequencyLast30Days(countSessionsInPeriod(history, 30))
                .progressionRate(computeProgressionRate(history))
                .difficultyLevel(exercise.getDifficultyLevel())
                .bodyPartMatchScore(1.0) // already filtered by body part
                .build();
    }

    private MlPerformanceFeatures buildPerformanceFeatures(Exercise exercise,
                                                           String username,
                                                           List<Drill> history,
                                                           Training training) {
        if (history.isEmpty()) {
            return MlPerformanceFeatures.builder()
                    .exerciseRefId(exercise.getRefId())
                    .username(username)
                    .lastWeight(0).lastReps(0).lastSets(DEFAULT_SETS)
                    .personalRecord(0).daysSinceLastSession(999)
                    .trendSlope(0).sessionsThisWeek(0)
                    .trainingType(training != null ? training.getName() : "UNKNOWN")
                    .build();
        }
        Drill last = history.get(0);
        return MlPerformanceFeatures.builder()
                .exerciseRefId(exercise.getRefId())
                .username(username)
                .lastWeight(last.getMeasurement())
                .lastReps(last.getRepetition())
                .lastSets(DEFAULT_SETS)
                .personalRecord(history.stream().mapToDouble(Drill::getMeasurement).max().orElse(0))
                .daysSinceLastSession(computeDaysSinceLastDone(history))
                .trendSlope(computeTrendSlope(history))
                .sessionsThisWeek(countSessionsInPeriod(history, 7))
                .trainingType(training != null ? training.getName() : "UNKNOWN")
                .build();
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private Map<Long, List<Drill>> loadHistory(String username, List<Exercise> candidates) {
        Map<Long, List<Drill>> history = new HashMap<>();
        for (Exercise ex : candidates) {
            history.put(ex.getRefId(),
                    drillRepository.findByUsernameAndExerciseOrderByRoutineDateDesc(username, ex));
        }
        return history;
    }

    private List<Exercise> fetchCandidates(BodyPart bodyPart, Training training) {
        return training != null
                ? exerciseRepository.findByTrainingAndTargetBodyPart(training, bodyPart)
                : exerciseRepository.findByTargetBodyPart(bodyPart);
    }

    // ── Scoring helpers ───────────────────────────────────────────────────────

    private double compositeScore(List<Drill> history) {
        int daysSince = computeDaysSinceLastDone(history);
        double neglect = computeNeglectScore(daysSince);
        double frequency = Math.max(0, 1.0 - countSessionsInPeriod(history, 30) / 5.0);
        return NEGLECT_WEIGHT * neglect + FREQUENCY_WEIGHT * frequency;
    }

    private int computeDaysSinceLastDone(List<Drill> history) {
        if (history.isEmpty()) return 999;
        LocalDate lastDate = history.get(0).getRoutineObj().getRoutineDate();
        return (int) ChronoUnit.DAYS.between(lastDate, LocalDate.now());
    }

    private double computeNeglectScore(int daysSince) {
        if (daysSince >= 999) return 1.0;
        return Math.min(1.0, (double) daysSince / NEGLECT_CEILING_DAYS);
    }

    private int countSessionsInPeriod(List<Drill> history, int days) {
        LocalDate cutoff = LocalDate.now().minusDays(days);
        return (int) history.stream()
                .filter(d -> d.getRoutineObj() != null
                        && !d.getRoutineObj().getRoutineDate().isBefore(cutoff))
                .count();
    }

    private double computeProgressionRate(List<Drill> history) {
        if (history.size() < 2) return 0.0;
        double first = history.get(history.size() - 1).getMeasurement();
        double last = history.get(0).getMeasurement();
        return Math.round(((last - first) / (first + 1e-9)) * 10000.0) / 10000.0;
    }

    private double computeTrendSlope(List<Drill> history) {
        List<Drill> last5 = history.stream().limit(5).toList();
        if (last5.size() < 2) return 0.0;
        double first = last5.get(last5.size() - 1).getMeasurement();
        double last = last5.get(0).getMeasurement();
        return Math.round(((last - first) / (last5.size() - 1)) * 100.0) / 100.0;
    }

    // ── Progressive overload (rule engine performance fallback) ───────────────

    private Progression calculateProgression(Exercise exercise, List<Drill> history) {
        if (history.isEmpty()) return starterProgression(exercise);

        Drill last = history.get(0);
        double lastWeight = last.getMeasurement();
        int lastReps = last.getRepetition();
        boolean stalled = history.size() >= 2
                && history.get(1).getMeasurement() == lastWeight
                && lastReps < TARGET_REPS_MIN;

        if (stalled) {
            double deload = round2dp(lastWeight * (1 - DELOAD_PCT));
            return new Progression(deload, DEFAULT_SETS, TARGET_REPS_MAX,
                    String.format("Deload → %.1f (−15%% from %.1f)", deload, lastWeight));
        }
        if (lastReps >= TARGET_REPS_MAX) {
            double increased = round2dp(lastWeight * (1 + INCREASE_PCT));
            return new Progression(increased, DEFAULT_SETS, TARGET_REPS_MIN,
                    String.format("Progressive increase → %.1f (+2.5%% from %.1f)", increased, lastWeight));
        }
        return new Progression(lastWeight, DEFAULT_SETS, Math.min(lastReps + 1, TARGET_REPS_MAX),
                String.format("Maintain %.1f — aim for %d reps", lastWeight, Math.min(lastReps + 1, TARGET_REPS_MAX)));
    }

    private Progression starterProgression(Exercise exercise) {
        int diff = exercise.getDifficultyLevel();
        double weight = diff <= 1 ? 10.0 : diff == 2 ? 15.0 : diff == 3 ? 20.0 : diff == 4 ? 30.0 : 40.0;
        return new Progression(weight, DEFAULT_SETS, TARGET_REPS_MAX,
                "No history — starter weight " + weight + " " + exercise.getUnit());
    }

    // ── Entity resolution ─────────────────────────────────────────────────────

    private BodyPart resolveBodyPart(String refId) {
        if (StringUtils.isBlank(refId)) throw new ServerException().new InternalError("targetBodyPartRefId is required");
        BodyPart bp = bodyPartsRepository.findByRefId(Long.parseLong(refId));
        if (bp == null) throw new ServerException().new InternalError("Body part not found: " + refId);
        return bp;
    }

    private Training resolveTraining(String refId) {
        if (StringUtils.isBlank(refId)) return null;
        Training t = trainingRepository.findByRefId(Long.parseLong(refId));
        if (t == null) throw new ServerException().new InternalError("Training type not found: " + refId);
        return t;
    }

    private double round2dp(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record Progression(double weight, int sets, int reps, String note) {}
}
