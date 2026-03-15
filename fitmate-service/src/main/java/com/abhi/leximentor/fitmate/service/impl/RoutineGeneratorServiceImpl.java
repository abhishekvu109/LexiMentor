package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.MeasurementUnit;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineGenerationDTO;
import com.abhi.leximentor.fitmate.entities.*;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.ExerciseMapper;
import com.abhi.leximentor.fitmate.mapper.TrainingMapper;
import com.abhi.leximentor.fitmate.repository.*;
import com.abhi.leximentor.fitmate.service.RoutineGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoutineGeneratorServiceImpl implements RoutineGeneratorService {

    private final DrillRepository drillRepository;
    private final RoutineRepository routineRepository;
    private final TrainingRepository trainingRepository;
    private final BodyPartsRepository bodyPartsRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingMapper trainingMapper;
    private final ExerciseMapper exerciseMapper;

    private static final int MIN_EXERCISES_PER_ROUTINE = 8;      // Increased from 5
    private static final int MAX_EXERCISES_PER_ROUTINE = 12;     // New upper cap
    private static final int MIN_EXERCISES_FULL_BODY = 10;       // For balance
    private static final int MAX_EXERCISES_FULL_BODY = 15;       // Allow more volume
    private static final int STALENESS_BOOST_FOR_NEGLECTED_BODY_PART = 50;
    private static final int DAYS_FOR_NEGLECTED_BODY_PART = 7;
    private static final int MAX_PER_MUSCLE = 3;                 // Increased from 2
    private static final int EXERCISES_PER_BODY_PART_FULL_BODY = 2;  // Increased from 1 (range: 2-4)
    private static final long NEVER_TRAINED_SCORE = 1000L;
    private static final double RANDOMNESS_FACTOR = 0.3;         // 30% randomness in weighted selection

    @Override
    @Transactional
    public RoutineDTO generateRoutine(RoutineGenerationDTO dto) {
        String trainingType = dto.getTrainingType();
        List<String> targetBodyParts = dto.getTargetBodyParts();
        String username = dto.getUsername();
        if (StringUtils.isAnyEmpty(trainingType, username) && CollectionUtils.isEmpty(targetBodyParts)) {
            throw new ServerException().new InternalError("Training type, username and target body parts list are required.");
        }

        // Normalize list (uppercase, trim)
        List<String> normalizedTargets = targetBodyParts.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        boolean isFullBody = normalizedTargets.contains("FULL_BODY") || normalizedTargets.isEmpty() || normalizedTargets.size() > 5;  // Heuristic for "all"

        // Step 1: Find or create Training (same as before)
        Training training = trainingRepository.findByNameIgnoreCase(trainingType);
        if (training == null) {
            throw new ServerException().new EntityObjectNotFound("Training object is not found.");
        }

        // Step 2: Get relevant BodyParts
        List<BodyPart> relevantBodyParts = getRelevantBodyParts(normalizedTargets, isFullBody);

        if (relevantBodyParts.isEmpty()) {
            throw new ServerException().new EntityObjectNotFound("No valid body parts found.");
        }

        // Step 3: Get candidate exercises
        List<Exercise> exercises = fetchExercisesForBodyParts(relevantBodyParts);

        if (exercises.isEmpty()) {
            throw new ServerException().new EntityObjectNotFound("No exercises found for the specified body parts.");
        }

        // Step 4: Check neglected status (per body part or average for full body)
        Map<BodyPart, Boolean> neglectedMap = buildNeglectedMap(relevantBodyParts, isFullBody,username);

        // Step 5: Calculate staleness and select balanced exercises
        List<Exercise> selectedExercises = selectBalancedExercises(exercises, neglectedMap, isFullBody, relevantBodyParts);

        // Step 6: Create drills
        List<DrillDTO> drillDtos = selectedExercises
                .stream()
                .sorted(Comparator.comparing(
                        ex -> ex.getTargetBodyPart().getName(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(exercise -> buildDefaultDrillDto(exercise, trainingType))
                .collect(Collectors.toList());

        // Step 7: Create and save routine
        String description = String.format("Auto-generated %s routine for %s",
                isFullBody ? "full body" : String.join(", ", normalizedTargets), trainingType);
        RoutineDTO routineDto = RoutineDTO.builder()
                .description(description)
                .status(Status.RoutineStatus.toString(Status.RoutineStatus.NOT_STARTED))
                .workoutDate(LocalDate.now().toString())
                .training(trainingMapper.toDto(training))
                .drills(drillDtos)
                .burntCalories(0)
                .durationInMinutes(0)
                .build();

        return routineDto;
    }

    private List<BodyPart> getRelevantBodyParts(List<String> normalizedTargets, boolean isFullBody) {
        if (isFullBody) {
            return bodyPartsRepository.findAll();  // All body parts
        }
        return normalizedTargets.stream()
                .map(target -> bodyPartsRepository.findByName(target))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private Map<BodyPart, Boolean> buildNeglectedMap(List<BodyPart> bodyParts, boolean isFullBody,String username) {
        Map<BodyPart, Boolean> neglectedMap = new HashMap<>();
        List<Routine> recentRoutines = routineRepository.findByUsernameOrderByRoutineDateDesc(username);
        for (BodyPart bp : bodyParts) {
            boolean neglected = isBodyPartNeglected(bp, recentRoutines);
            neglectedMap.put(bp, neglected);
        }
        if (isFullBody) {
            // For full body, use average: if >50% neglected, boost all
            boolean globalNeglected = neglectedMap.values().stream().filter(Boolean::booleanValue).count() > (bodyParts.size() / 2);
            neglectedMap.replaceAll((k, v) -> globalNeglected);
        }
        return neglectedMap;
    }

    private boolean isBodyPartNeglected(BodyPart bodyPart, List<Routine> recentRoutines) {
        Optional<LocalDate> lastTrained = recentRoutines.stream()
                .flatMap(r -> r.getDrills().stream())
                .filter(d -> d.getExercise().getTargetBodyPart().getId() == bodyPart.getId())
                .map(d -> d.getCrtnDate().toLocalDate())
                .max(Comparator.naturalOrder());
        return lastTrained.map(date -> ChronoUnit.DAYS.between(date, LocalDate.now()) > DAYS_FOR_NEGLECTED_BODY_PART)
                .orElse(true);
    }

    private List<Exercise> calculateStalenessAndSort(List<Exercise> exercises, boolean applyNeglectBoost) {
        Map<Exercise, Long> stalenessMap = exercises.stream().collect(Collectors.toMap(
                ex -> ex,
                ex -> {
                    List<Drill> drills = drillRepository.findByExerciseOrderByCrtnDateDesc(ex);
                    long score = drills.isEmpty() ? NEVER_TRAINED_SCORE
                            : ChronoUnit.DAYS.between(drills.get(0).getCrtnDate().toLocalDate(), LocalDate.now());
                    if (applyNeglectBoost) score += STALENESS_BOOST_FOR_NEGLECTED_BODY_PART;
                    return score;
                }
        ));

        return exercises.stream()
                .sorted(Comparator.comparingLong(stalenessMap::get).reversed())
                .collect(Collectors.toList());
    }

    private DrillDTO buildDefaultDrillDto(Exercise exercise, String trainingType) {
        int reps = exercise.getUnit().toLowerCase().contains("time") ? 3 : 10;
        double measurement = trainingType.equalsIgnoreCase("yoga") ? 0 : 50;
        String unit = StringUtils.isNotBlank(exercise.getUnit()) ? exercise.getUnit() : "reps";
        String scale = MeasurementUnit.Repetition.REPS;
        if (StringUtils.equalsIgnoreCase(unit, "weight")) {
            scale = MeasurementUnit.Weight.KILOGRAM;
        } else if (StringUtils.equalsIgnoreCase(unit, "time")) {
            scale = MeasurementUnit.Time.SECOND;
        }
        Muscle muscle = exercise.getTargetMuscles().isEmpty() ? null : exercise.getTargetMuscles().get(0);

        return DrillDTO.builder()
                .exercise(exerciseMapper.toDto(exercise))
                .measurementUnit(unit)
                .measurement(measurement)
                .unit(scale)
                .repetition(reps)
                .burntCalories(0)
                .creationDate(LocalDateTime.now())
                .notes("Auto-generated drill")
//                .muscle(muscle != null ? FitmateServiceUtil.MuscleUtil.buildDTO(muscle, exercise.getTargetBodyPart()) : null)
                .build();
    }

    private List<Exercise> fetchExercisesForBodyParts(List<BodyPart> bodyParts) {
        return bodyParts.stream()
                .flatMap(bp -> exerciseRepository.findByTargetBodyPartRefId(bp.getRefId()).stream())
                .collect(Collectors.toList());
    }

    private List<Exercise> selectBalancedExercises(List<Exercise> exercises, Map<BodyPart, Boolean> neglectedMap,
                                                   boolean isFullBody, List<BodyPart> relevantBodyParts) {
        // Step 1: Calculate staleness for all (with neglect boost)
        Map<Exercise, Double> weightedScores = new HashMap<>();  // Now double for probabilities
        for (Exercise ex : exercises) {
            List<Drill> drills = drillRepository.findByExerciseOrderByCrtnDateDesc(ex);
            long baseScore = drills.isEmpty() ? NEVER_TRAINED_SCORE
                    : ChronoUnit.DAYS.between(drills.get(0).getCrtnDate().toLocalDate(), LocalDate.now());
            boolean neglected = neglectedMap.get(ex.getTargetBodyPart());
            double finalScore = baseScore + (neglected ? STALENESS_BOOST_FOR_NEGLECTED_BODY_PART : 0);
            // Add difficulty progression: +20 if last drill was "easy" (e.g., low reps <8)
            if (!drills.isEmpty() && drills.get(0).getRepetition() < 8) {
                finalScore += 20;
            }
            weightedScores.put(ex, Math.max(finalScore, 1));  // Min 1 to avoid div-by-zero
        }

        // Step 2: Group by body part for full body
        Map<BodyPart, List<Exercise>> exercisesByBodyPart = exercises.stream()
                .collect(Collectors.groupingBy(Exercise::getTargetBodyPart));

        List<Exercise> initialCandidates = new ArrayList<>();
        int targetCount = isFullBody ? ThreadLocalRandom.current().nextInt(MIN_EXERCISES_FULL_BODY, MAX_EXERCISES_FULL_BODY + 1)
                : ThreadLocalRandom.current().nextInt(MIN_EXERCISES_PER_ROUTINE, MAX_EXERCISES_PER_ROUTINE + 1);

        if (isFullBody) {
            // Balanced full body: 2-4 per part, randomized
            for (BodyPart bp : relevantBodyParts) {
                List<Exercise> bpExercises = exercisesByBodyPart.getOrDefault(bp, new ArrayList<>());
                if (bpExercises.isEmpty()) continue;

                // Dynamic per-part count: Larger parts (e.g., legs) get more
                int perPartCount = bp.getName().toLowerCase().contains("legs") || bp.getName().toLowerCase().contains("back")
                        ? ThreadLocalRandom.current().nextInt(3, 5)  // 3-4 for big parts
                        : ThreadLocalRandom.current().nextInt(2, 4); // 2-3 for smaller
                perPartCount = Math.min(perPartCount, bpExercises.size());

                // Weighted random pick per part
                List<Exercise> topPerPart = weightedRandomSelect(bpExercises, weightedScores, perPartCount);
                initialCandidates.addAll(topPerPart);
            }
        } else {
            // Specific: Weighted random from all
            initialCandidates = weightedRandomSelect(exercises, weightedScores, targetCount);
        }

        // Step 3: Apply muscle variety filter (softer now: warn but don't hard-block if variety low)
        List<Exercise> selected = applyMuscleVarietyFilter(initialCandidates);

        // If variety filter reduced too much, add back randomly from candidates
        while (selected.size() < Math.max(6, targetCount / 2)) {  // Ensure min 6
            Exercise filler = weightedRandomSelect(initialCandidates, weightedScores, 1).get(0);
            if (!selected.contains(filler)) selected.add(filler);
        }

        return selected.stream().limit(targetCount).collect(Collectors.toList());
    }

    // Helper: Weighted random selection (injects variety)
    private List<Exercise> weightedRandomSelect(List<Exercise> candidates, Map<Exercise, Double> scores, int count) {
        if (candidates.size() <= count) return new ArrayList<>(candidates);

        // Normalize to probabilities
        double totalWeight = scores.entrySet().stream()
                .filter(e -> candidates.contains(e.getKey()))
                .mapToDouble(Map.Entry::getValue)
                .sum();

        List<Exercise> selected = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double rand = ThreadLocalRandom.current().nextDouble(totalWeight * RANDOMNESS_FACTOR);  // Bias toward high scores
            double cumulative = 0;
            for (Map.Entry<Exercise, Double> entry : scores.entrySet()) {
                if (!candidates.contains(entry.getKey()) || selected.contains(entry.getKey())) continue;
                cumulative += entry.getValue();
                if (cumulative >= rand) {
                    selected.add(entry.getKey());
                    break;
                }
            }
        }
        return selected;
    }

    // Softer variety filter (increased cap, allows some repetition if needed)
    private List<Exercise> applyMuscleVarietyFilter(List<Exercise> candidates) {
        Map<Long, Integer> muscleCount = new HashMap<>();
        List<Exercise> selected = new ArrayList<>();
        for (Exercise ex : candidates) {
            if (selected.size() >= candidates.size()) break;
            Long muscleId = ex.getTargetMuscles().isEmpty() ? 0L : ex.getTargetMuscles().get(0).getId();
            int count = muscleCount.getOrDefault(muscleId, 0);
            if (count < MAX_PER_MUSCLE) {
                selected.add(ex);
                muscleCount.put(muscleId, count + 1);
            } else {
                // Soft skip: Only skip 20% of the time for variety
                if (ThreadLocalRandom.current().nextDouble() > 0.2) {
                    selected.add(ex);  // Allow occasional repeat
                }
            }
        }
        return selected;
    }
}