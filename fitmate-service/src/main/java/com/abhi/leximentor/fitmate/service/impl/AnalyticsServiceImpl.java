package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.DashboardSummaryDTO;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseFrequencyDTO;
import com.abhi.leximentor.fitmate.dto.WorkoutTrendDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.repository.*;
import com.abhi.leximentor.fitmate.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RoutineRepository routineRepository;
    private final DrillRepository drillRepository;
    private final ExerciseRepository exerciseRepository;
    private final BodyPartsRepository bodyPartsRepository;
    private final MuscleRepository muscleRepository;
    private final TrainingRepository trainingRepository;


    @Autowired
    public AnalyticsServiceImpl(RoutineRepository routineRepository, DrillRepository drillRepository, ExerciseRepository exerciseRepository, BodyPartsRepository bodyPartsRepository, MuscleRepository muscleRepository, TrainingRepository trainingRepository) {
        this.routineRepository = routineRepository;
        this.drillRepository = drillRepository;
        this.exerciseRepository = exerciseRepository;
        this.bodyPartsRepository = bodyPartsRepository;
        this.muscleRepository = muscleRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getOverallAnalytics(String username) {
        log.info("Initiating overall analytics for user: {}", username);

        // Fetch all routines for the given username
        List<Routine> userRoutines = routineRepository.findByUsername(username);

        // 1. Calculate DashboardSummaryDTO
        DashboardSummaryDTO summary = calculateDashboardSummary(userRoutines);

        // 2. Calculate WorkoutTrendDTOs
        List<WorkoutTrendDTO> workoutTrends = calculateWorkoutTrends(userRoutines);

        // 3. Calculate BodyPartWorkoutVolume
        Map<String, Long> bodyPartWorkoutVolume = calculateBodyPartWorkoutVolume(userRoutines);

        // 4. Calculate ExerciseAnalyticsDTOs
        Map<String, com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO> exerciseAnalytics = calculateExerciseAnalytics(userRoutines);

        // 5. Calculate Most Frequent Exercises
        List<ExerciseFrequencyDTO> mostFrequentExercises = calculateMostFrequentExercises(userRoutines);

        // 6. Calculate Routine Distribution by Training Type
        Map<String, Long> routineDistributionByTrainingType = calculateRoutineDistributionByTrainingType(userRoutines);


        return AnalyticsDTO.builder()
                .summary(summary)
                .workoutTrends(workoutTrends)
                .bodyPartWorkoutVolume(bodyPartWorkoutVolume)
                .exerciseAnalytics(exerciseAnalytics)
                .mostFrequentExercises(mostFrequentExercises) // Populate new field
                .routineDistributionByTrainingType(routineDistributionByTrainingType) // Populate new field
                .build();
    }

    private DashboardSummaryDTO calculateDashboardSummary(List<Routine> userRoutines) {
        long totalRoutinesCompleted = userRoutines.size();
        double totalWorkoutDurationMinutes = userRoutines.stream().mapToDouble(Routine::getDurationInMinutes).sum();
        double totalCaloriesBurnt = userRoutines.stream().mapToDouble(Routine::getBurntCalories).sum();

        long totalUniqueExercises = userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .map(Drill::getExercise)
                .map(Exercise::getName)
                .distinct()
                .count();

        return DashboardSummaryDTO.builder()
                .totalRoutinesCompleted(totalRoutinesCompleted)
                .totalWorkoutDurationMinutes(totalWorkoutDurationMinutes)
                .totalCaloriesBurnt(totalCaloriesBurnt)
                .totalUniqueExercises(totalUniqueExercises)
                .build();
    }

    private List<WorkoutTrendDTO> calculateWorkoutTrends(List<Routine> userRoutines) {
        Map<LocalDate, List<Routine>> routinesByDate = userRoutines.stream()
                .collect(Collectors.groupingBy(Routine::getRoutineDate));

        return routinesByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Routine> routinesOnDate = entry.getValue();
                    long routinesCompleted = routinesOnDate.size();
                    double workoutDurationMinutes = routinesOnDate.stream().mapToDouble(Routine::getDurationInMinutes).sum();
                    double caloriesBurnt = routinesOnDate.stream().mapToDouble(Routine::getBurntCalories).sum();
                    return WorkoutTrendDTO.builder()
                            .date(date)
                            .routinesCompleted(routinesCompleted)
                            .workoutDurationMinutes(workoutDurationMinutes)
                            .caloriesBurnt(caloriesBurnt)
                            .build();
                })
                .sorted(Comparator.comparing(WorkoutTrendDTO::getDate))
                .collect(Collectors.toList());
    }

    private Map<String, Long> calculateBodyPartWorkoutVolume(List<Routine> userRoutines) {
        return userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .map(Drill::getExercise)
                .map(exercise -> exercise.getTargetBodyPart().getName())
                .collect(Collectors.groupingBy(bodyPartName -> bodyPartName, Collectors.counting()));
    }

    private Map<String, com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO> calculateExerciseAnalytics(List<Routine> userRoutines) {
        return userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .collect(Collectors.groupingBy(drill -> drill.getExercise().getName(),
                        Collectors.mapping(drill -> drill, Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Drill> drillsForExercise = entry.getValue();
                            long totalNumberOfTimesCompleted = drillsForExercise.size();

                            double monthlyAverage = drillsForExercise.stream()
                                    .filter(drill -> drill.getCrtnDate().getMonth() == LocalDateTime.now().getMonth() &&
                                            drill.getCrtnDate().getYear() == LocalDateTime.now().getYear())
                                    .count();

                            List<DrillDTO> lastFiveDrills = drillsForExercise.stream()
                                    .sorted(Comparator.comparing(Drill::getCrtnDate).reversed())
                                    .limit(5)
                                    .map(FitmateServiceUtil.DrillUtil::buildDTO)
                                    .collect(Collectors.toList());

                            // Calculate maxMeasurement and maxRepetitions
                            Optional<Drill> maxMeasurementDrill = drillsForExercise.stream()
                                    .max(Comparator.comparingDouble(Drill::getMeasurement));
                            double maxMeasurement = maxMeasurementDrill.map(Drill::getMeasurement).orElse(0.0);

                            Optional<Drill> maxRepetitionsDrill = drillsForExercise.stream()
                                    .max(Comparator.comparingInt(Drill::getRepetition));
                            int maxRepetitions = maxRepetitionsDrill.map(Drill::getRepetition).orElse(0);

                            return com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO.builder()
                                    .totalNumberOfTimesCompleted((int) totalNumberOfTimesCompleted)
                                    .monthlyAverage(monthlyAverage)
                                    .lastFiveDrills(lastFiveDrills)
                                    .maxMeasurement(maxMeasurement)
                                    .maxRepetitions(maxRepetitions)
                                    .build();
                        }
                ));
    }

    private List<ExerciseFrequencyDTO> calculateMostFrequentExercises(List<Routine> userRoutines) {
        return userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .map(Drill::getExercise)
                .map(Exercise::getName)
                .collect(Collectors.groupingBy(exerciseName -> exerciseName, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> ExerciseFrequencyDTO.builder()
                        .exerciseName(entry.getKey())
                        .frequency(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ExerciseFrequencyDTO::getFrequency).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private Map<String, Long> calculateRoutineDistributionByTrainingType(List<Routine> userRoutines) {
        return userRoutines.stream()
                .filter(routine -> routine.getTraining() != null)
                .map(routine -> routine.getTraining().getName())
                .collect(Collectors.groupingBy(trainingName -> trainingName, Collectors.counting()));
    }
}