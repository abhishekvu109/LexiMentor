package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.MeasurementUnit;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineGenerationDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.entities.Training;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.repository.TrainingRepository;
import com.abhi.leximentor.fitmate.service.RoutineGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdaptiveRoutineGeneratorServiceImpl implements RoutineGeneratorService {

    private final RoutineRepository routineRepository;
    private final TrainingRepository trainingRepository;
    private final BodyPartsRepository bodyPartsRepository;
    private final ExerciseRepository exerciseRepository;

    private static final int MIN_EXERCISES = 6;
    private static final int MAX_EXERCISES = 12;
    private static final int FULL_BODY_MIN_EXERCISES = 8;
    private static final int FULL_BODY_MAX_EXERCISES = 14;
    private static final int NEGLECT_DAYS_THRESHOLD = 14;
    private static final int LONG_BREAK_DAYS = 45;
    private static final int RECENT_DAYS = 7;
    private static final double RANDOMNESS = 0.25;

    private enum UnitType {
        WEIGHT,
        TIME,
        REPS
    }

    @Override
    @Transactional
    public RoutineDTO generateRoutine(RoutineGenerationDTO dto) {
        String trainingType = dto.getTrainingType();
        List<String> targetBodyParts = dto.getTargetBodyParts();
        String username = dto.getUsername();
        if (StringUtils.isAnyEmpty(trainingType, username) && CollectionUtils.isEmpty(targetBodyParts)) {
            throw new ServerException().new InternalError("Training type, username and target body parts list are required.");
        }

        List<String> normalizedTargets = normalizeTargets(targetBodyParts);
        boolean isFullBody = isFullBody(normalizedTargets);

        Training training = trainingRepository.findByNameIgnoreCase(trainingType);
        if (training == null) {
            throw new ServerException().new EntityObjectNotFound("Training object is not found.");
        }

        List<BodyPart> relevantBodyParts = resolveBodyParts(normalizedTargets, isFullBody);
        if (relevantBodyParts.isEmpty()) {
            throw new ServerException().new EntityObjectNotFound("No valid body parts found.");
        }

        List<Exercise> candidates = fetchExercises(relevantBodyParts);
        if (candidates.isEmpty()) {
            throw new ServerException().new EntityObjectNotFound("No exercises found for the specified body parts.");
        }

        List<Routine> userRoutines = routineRepository.findByUsernameOrderByRoutineDateDesc(username);
        HistorySnapshot history = buildHistorySnapshot(userRoutines);

        List<Exercise> selectedExercises = selectExercises(
                candidates,
                relevantBodyParts,
                history,
                isFullBody,
                trainingType
        );

        List<DrillDTO> drillDtos = selectedExercises.stream()
                .map(exercise -> buildAdaptiveDrill(exercise, trainingType, history))
                .collect(Collectors.toList());

        String description = String.format("Auto-generated %s routine for %s",
                isFullBody ? "full body" : String.join(", ", normalizedTargets), trainingType);
        return RoutineDTO.builder()
                .description(description)
                .status(Status.RoutineStatus.toString(Status.RoutineStatus.NOT_STARTED))
                .workoutDate(LocalDate.now().toString())
                .training(FitmateServiceUtil.TrainingMetadataUtil.buildDto(training))
                .drills(drillDtos)
                .burntCalories(0)
                .durationInMinutes(0)
                .build();
    }

    private List<String> normalizeTargets(List<String> targets) {
        if (targets == null) {
            return List.of();
        }
        return targets.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(value -> value.toUpperCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isFullBody(List<String> normalizedTargets) {
        if (normalizedTargets.isEmpty()) {
            return true;
        }
        return normalizedTargets.contains("FULL_BODY") || normalizedTargets.contains("FULL BODY") || normalizedTargets.contains("ALL");
    }

    private List<BodyPart> resolveBodyParts(List<String> normalizedTargets, boolean isFullBody) {
        if (isFullBody) {
            return bodyPartsRepository.findAll();
        }
        return normalizedTargets.stream()
                .map(bodyPartsRepository::findByNameIgnoreCase)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Exercise> fetchExercises(List<BodyPart> bodyParts) {
        return bodyParts.stream()
                .flatMap(bp -> exerciseRepository.findByTargetBodyPartRefId(bp.getRefId()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private HistorySnapshot buildHistorySnapshot(List<Routine> routines) {
        Map<Long, DrillHistory> historyByExercise = new HashMap<>();
        Map<Long, LocalDate> lastBodyPartDate = new HashMap<>();
        Set<Long> lastRoutineExercises = new HashSet<>();

        Routine lastRoutine = routines.stream()
                .filter(routine -> routine.getDrills() != null && !routine.getDrills().isEmpty())
                .findFirst()
                .orElse(null);

        if (lastRoutine != null) {
            for (Drill drill : lastRoutine.getDrills()) {
                if (drill != null && drill.getExercise() != null) {
                    lastRoutineExercises.add(drill.getExercise().getId());
                }
            }
        }

        for (Routine routine : routines) {
            if (routine.getDrills() == null) {
                continue;
            }
            for (Drill drill : routine.getDrills()) {
                if (drill == null || drill.getExercise() == null) {
                    continue;
                }
                Exercise exercise = drill.getExercise();
                LocalDateTime drillDateTime = drill.getCrtnDate();
                LocalDate drillDate = drillDateTime != null
                        ? drillDateTime.toLocalDate()
                        : routine.getRoutineDate() != null ? routine.getRoutineDate() : LocalDate.now();

                DrillHistory history = historyByExercise.computeIfAbsent(exercise.getId(), id -> new DrillHistory());
                history.count += 1;
                if (history.lastDrill == null || isAfter(drillDateTime, history.lastDateTime)) {
                    history.lastDrill = drill;
                    history.lastDate = drillDate;
                    history.lastDateTime = drillDateTime;
                }

                BodyPart bodyPart = exercise.getTargetBodyPart();
                if (bodyPart != null) {
                    LocalDate lastDate = lastBodyPartDate.get(bodyPart.getId());
                    if (lastDate == null || drillDate.isAfter(lastDate)) {
                        lastBodyPartDate.put(bodyPart.getId(), drillDate);
                    }
                }
            }
        }

        return new HistorySnapshot(historyByExercise, lastBodyPartDate, lastRoutineExercises);
    }

    private boolean isAfter(LocalDateTime candidate, LocalDateTime current) {
        if (candidate == null && current == null) {
            return false;
        }
        if (candidate == null) {
            return false;
        }
        if (current == null) {
            return true;
        }
        return candidate.isAfter(current);
    }

    private List<Exercise> selectExercises(List<Exercise> candidates,
                                           List<BodyPart> bodyParts,
                                           HistorySnapshot history,
                                           boolean isFullBody,
                                           String trainingType) {
        int targetCount = isFullBody
                ? clamp(bodyParts.size() * 2, FULL_BODY_MIN_EXERCISES, FULL_BODY_MAX_EXERCISES)
                : clamp(Math.max(1, bodyParts.size()) * 3, MIN_EXERCISES, MAX_EXERCISES);

        Set<Long> lastRoutineExercises = history.lastRoutineExercises;
        List<Exercise> filteredCandidates = new ArrayList<>(candidates);
        if (filteredCandidates.size() - lastRoutineExercises.size() >= targetCount) {
            filteredCandidates = filteredCandidates.stream()
                    .filter(exercise -> !lastRoutineExercises.contains(exercise.getId()))
                    .collect(Collectors.toList());
        }

        Map<Long, Double> scores = scoreExercises(filteredCandidates, history, trainingType);

        List<Exercise> selected;
        if (isFullBody) {
            selected = selectForFullBody(filteredCandidates, bodyParts, scores, targetCount);
        } else {
            selected = weightedPick(filteredCandidates, scores, targetCount);
        }

        return roundRobinByBodyPart(selected);
    }

    private Map<Long, Double> scoreExercises(List<Exercise> candidates, HistorySnapshot history, String trainingType) {
        Map<Long, Double> scores = new HashMap<>();
        for (Exercise exercise : candidates) {
            DrillHistory drillHistory = history.historyByExercise.get(exercise.getId());
            double score = 1.0;
            if (drillHistory == null) {
                score += 1000;
            } else {
                long daysSince = drillHistory.lastDate == null
                        ? LONG_BREAK_DAYS
                        : ChronoUnit.DAYS.between(drillHistory.lastDate, LocalDate.now());
                score += Math.min(daysSince * 2.0, 200.0);
                if (daysSince <= RECENT_DAYS) {
                    score -= 60;
                }
                if (daysSince >= LONG_BREAK_DAYS) {
                    score += 80;
                }
                if (drillHistory.count >= 5) {
                    score -= Math.min(drillHistory.count * 2.0, 30.0);
                }
            }

            BodyPart bodyPart = exercise.getTargetBodyPart();
            if (bodyPart != null) {
                LocalDate lastTrained = history.lastBodyPartDate.get(bodyPart.getId());
                if (lastTrained == null || ChronoUnit.DAYS.between(lastTrained, LocalDate.now()) >= NEGLECT_DAYS_THRESHOLD) {
                    score += 50;
                }
            }

            if (history.lastRoutineExercises.contains(exercise.getId())) {
                score -= 200;
            }

            if (exercise.getTraining() != null && StringUtils.isNotBlank(trainingType)) {
                if (exercise.getTraining().getName() != null
                        && exercise.getTraining().getName().equalsIgnoreCase(trainingType)) {
                    score += 20;
                } else {
                    score -= 10;
                }
            }

            scores.put(exercise.getId(), Math.max(score, 1.0));
        }
        return scores;
    }

    private List<Exercise> selectForFullBody(List<Exercise> candidates,
                                             List<BodyPart> bodyParts,
                                             Map<Long, Double> scores,
                                             int targetCount) {
        Map<Long, List<Exercise>> byBodyPart = candidates.stream()
                .filter(exercise -> exercise.getTargetBodyPart() != null)
                .collect(Collectors.groupingBy(exercise -> exercise.getTargetBodyPart().getId()));

        List<Exercise> selected = new ArrayList<>();
        int remaining = targetCount;
        for (BodyPart bodyPart : bodyParts) {
            List<Exercise> bodyPartExercises = byBodyPart.getOrDefault(bodyPart.getId(), List.of());
            if (bodyPartExercises.isEmpty()) {
                continue;
            }
            int baseCount = isMajorBodyPart(bodyPart.getName()) ? 2 : 1;
            int count = Math.min(baseCount, bodyPartExercises.size());
            selected.addAll(weightedPick(bodyPartExercises, scores, count));
            remaining -= count;
        }

        if (remaining > 0) {
            List<Exercise> leftovers = candidates.stream()
                    .filter(exercise -> !selected.contains(exercise))
                    .collect(Collectors.toList());
            selected.addAll(weightedPick(leftovers, scores, remaining));
        }

        return selected.stream().limit(targetCount).collect(Collectors.toList());
    }

    private boolean isMajorBodyPart(String name) {
        if (name == null) {
            return false;
        }
        String normalized = name.toLowerCase(Locale.ROOT);
        return normalized.contains("leg") || normalized.contains("back") || normalized.contains("chest");
    }

    private List<Exercise> weightedPick(List<Exercise> candidates, Map<Long, Double> scores, int count) {
        if (candidates.size() <= count) {
            return new ArrayList<>(candidates);
        }
        List<Exercise> available = new ArrayList<>(candidates);
        List<Exercise> selected = new ArrayList<>();
        for (int i = 0; i < count && !available.isEmpty(); i++) {
            double totalWeight = available.stream()
                    .mapToDouble(exercise -> scores.getOrDefault(exercise.getId(), 1.0))
                    .sum();
            double rand = ThreadLocalRandom.current().nextDouble(totalWeight * (1 + RANDOMNESS));
            double cumulative = 0;
            Exercise picked = null;
            for (Exercise exercise : available) {
                cumulative += scores.getOrDefault(exercise.getId(), 1.0);
                if (cumulative >= rand) {
                    picked = exercise;
                    break;
                }
            }
            if (picked == null) {
                picked = available.get(0);
            }
            selected.add(picked);
            available.remove(picked);
        }
        return selected;
    }

    private List<Exercise> roundRobinByBodyPart(List<Exercise> exercises) {
        Map<Long, Deque<Exercise>> byBodyPart = exercises.stream()
                .filter(exercise -> exercise.getTargetBodyPart() != null)
                .collect(Collectors.groupingBy(exercise -> exercise.getTargetBodyPart().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), ArrayDeque::new)));

        List<Exercise> ordered = new ArrayList<>();
        boolean progressed = true;
        while (progressed) {
            progressed = false;
            for (Deque<Exercise> queue : byBodyPart.values()) {
                if (!queue.isEmpty()) {
                    ordered.add(queue.poll());
                    progressed = true;
                }
            }
        }
        return ordered.isEmpty() ? exercises : ordered;
    }

    private DrillDTO buildAdaptiveDrill(Exercise exercise, String trainingType, HistorySnapshot history) {
        DrillHistory drillHistory = history.historyByExercise.get(exercise.getId());
        UnitType unitType = resolveUnitType(exercise.getUnit());
        String measurementUnit = StringUtils.defaultIfBlank(exercise.getUnit(), "reps");
        String unitScale = unitScaleFor(unitType, measurementUnit);

        DrillPlan plan = buildPlan(unitType, drillHistory);
        return DrillDTO.builder()
                .exercise(FitmateServiceUtil.ExerciseUtil.buildDto(exercise))
                .measurementUnit(measurementUnit)
                .measurement(plan.measurement)
                .unit(unitScale)
                .repetition(plan.reps)
                .burntCalories(0)
                .creationDate(LocalDateTime.now())
                .notes(buildNotes(trainingType, drillHistory))
                .build();
    }

    private DrillPlan buildPlan(UnitType unitType, DrillHistory history) {
        int reps = unitType == UnitType.TIME ? 3 : 10;
        double measurement = unitType == UnitType.TIME ? 30 : 0;

        if (history != null && history.lastDrill != null) {
            long daysSince = history.lastDate == null
                    ? LONG_BREAK_DAYS
                    : ChronoUnit.DAYS.between(history.lastDate, LocalDate.now());

            if (unitType == UnitType.WEIGHT) {
                double last = history.lastDrill.getMeasurement();
                measurement = adjustWeight(last, daysSince);
                reps = adjustReps(history.lastDrill.getRepetition(), daysSince);
            } else if (unitType == UnitType.TIME) {
                double last = history.lastDrill.getMeasurement();
                measurement = adjustTime(last, daysSince);
                reps = history.lastDrill.getRepetition() > 0 ? history.lastDrill.getRepetition() : reps;
            } else {
                reps = adjustReps(history.lastDrill.getRepetition(), daysSince);
            }
        } else {
            if (unitType == UnitType.WEIGHT) {
                measurement = 20;
            } else if (unitType == UnitType.TIME) {
                measurement = 30;
            } else {
                reps = 10;
            }
        }

        return new DrillPlan(reps, measurement);
    }

    private double adjustWeight(double last, long daysSince) {
        double base = last > 0 ? last : 20;
        double adjusted;
        if (daysSince <= RECENT_DAYS) {
            adjusted = base * 1.05;
        } else if (daysSince <= 21) {
            adjusted = base * 1.02;
        } else if (daysSince >= LONG_BREAK_DAYS) {
            adjusted = base * 0.9;
        } else {
            adjusted = base;
        }
        return roundToHalf(Math.max(adjusted, 2.5));
    }

    private double adjustTime(double last, long daysSince) {
        double base = last > 0 ? last : 30;
        double adjusted;
        if (daysSince <= RECENT_DAYS) {
            adjusted = base * 1.05;
        } else if (daysSince >= LONG_BREAK_DAYS) {
            adjusted = base * 0.9;
        } else {
            adjusted = base;
        }
        return Math.max(10, Math.round(adjusted));
    }

    private int adjustReps(int last, long daysSince) {
        int base = last > 0 ? last : 10;
        if (daysSince <= RECENT_DAYS) {
            return base + 1;
        }
        if (daysSince >= LONG_BREAK_DAYS) {
            return Math.max(6, base - 2);
        }
        return base;
    }

    private double roundToHalf(double value) {
        return Math.round(value * 2.0) / 2.0;
    }

    private UnitType resolveUnitType(String unit) {
        if (StringUtils.isBlank(unit)) {
            return UnitType.REPS;
        }
        String normalized = unit.toLowerCase(Locale.ROOT);
        if (normalized.contains("weight") || normalized.contains("kg") || normalized.contains("lb")) {
            return UnitType.WEIGHT;
        }
        if (normalized.contains("time") || normalized.contains("sec") || normalized.contains("min")) {
            return UnitType.TIME;
        }
        return UnitType.REPS;
    }

    private String unitScaleFor(UnitType unitType, String measurementUnit) {
        if (unitType == UnitType.WEIGHT) {
            return MeasurementUnit.Weight.KILOGRAM;
        }
        if (unitType == UnitType.TIME) {
            String normalized = measurementUnit == null ? "" : measurementUnit.toLowerCase(Locale.ROOT);
            if (normalized.contains("min")) {
                return MeasurementUnit.Time.MINUTE;
            }
            if (normalized.contains("hr")) {
                return MeasurementUnit.Time.HOUR;
            }
            return MeasurementUnit.Time.SECOND;
        }
        return MeasurementUnit.Repetition.REPS;
    }

    private String buildNotes(String trainingType, DrillHistory history) {
        if (history == null || history.lastDate == null) {
            return String.format("Auto-generated drill for %s. New exercise.", trainingType);
        }
        long daysSince = ChronoUnit.DAYS.between(history.lastDate, LocalDate.now());
        if (daysSince >= LONG_BREAK_DAYS) {
            return String.format("Auto-generated drill for %s. Returning after %d days.", trainingType, daysSince);
        }
        if (daysSince <= RECENT_DAYS) {
            return String.format("Auto-generated drill for %s. Progressive load.", trainingType);
        }
        return String.format("Auto-generated drill for %s.", trainingType);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class DrillHistory {
        private Drill lastDrill;
        private LocalDate lastDate;
        private LocalDateTime lastDateTime;
        private int count;
    }

    private record HistorySnapshot(Map<Long, DrillHistory> historyByExercise,
                                   Map<Long, LocalDate> lastBodyPartDate,
                                   Set<Long> lastRoutineExercises) {
    }

    private record DrillPlan(int reps, double measurement) {
    }
}
