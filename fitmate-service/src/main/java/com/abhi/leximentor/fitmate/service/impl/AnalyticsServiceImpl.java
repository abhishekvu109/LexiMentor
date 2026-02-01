package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.DashboardSummaryDTO;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
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
import java.util.List;
import java.util.Map;
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

        // 4. Calculate ExerciseAnalyticsDTOs (Placeholder for now)
        // This would typically involve more complex aggregations per exercise
        Map<String, com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO> exerciseAnalytics = calculateExerciseAnalytics(userRoutines);


        return AnalyticsDTO.builder()
                .summary(summary)
                .workoutTrends(workoutTrends)
                .bodyPartWorkoutVolume(bodyPartWorkoutVolume)
                .build();
    }

    private DashboardSummaryDTO calculateDashboardSummary(List<Routine> userRoutines) {
        long totalRoutinesCompleted = userRoutines.size();
        double totalWorkoutDurationMinutes = userRoutines.stream().mapToDouble(Routine::getDurationInMinutes).sum();
        double totalCaloriesBurnt = userRoutines.stream().mapToDouble(Routine::getBurntCalories).sum();

        // To get unique exercises, we need to go through drills
        long totalUniqueExercises = userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .map(Drill::getExercise)
                .map(Exercise::getName) // Assuming exercise name is unique enough for this purpose
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
                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                .collect(Collectors.toList());
    }

    private Map<String, Long> calculateBodyPartWorkoutVolume(List<Routine> userRoutines) {
        return userRoutines.stream()
                .flatMap(routine -> routine.getDrills().stream())
                .map(Drill::getExercise)
                .map(exercise -> exercise.getTargetBodyPart().getName()) // Assuming targetBodyPart is always present
                .collect(Collectors.groupingBy(bodyPartName -> bodyPartName, Collectors.counting()));
    }

    private Map<String, com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO> calculateExerciseAnalytics(List<Routine> userRoutines) {
        // This is a more complex calculation that would involve:
        // 1. Grouping drills by exercise
        // 2. For each exercise, calculate total times completed, monthly average, and last five drills.
        // For brevity, this is a placeholder. A full implementation would query DrillRepository based on exercise and user.
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
                            // Calculate monthly average (simplified for now)
                            double monthlyAverage = drillsForExercise.stream()
                                    .filter(drill -> drill.getCrtnDate().getMonth() == LocalDateTime.now().getMonth() &&
                                            drill.getCrtnDate().getYear() == LocalDateTime.now().getYear())
                                    .count();
                            // Last five drills (simplified, needs proper sorting and limiting)
                            List<com.abhi.leximentor.fitmate.dto.DrillDTO> lastFiveDrills = drillsForExercise.stream()
                                    .sorted((d1, d2) -> d2.getCrtnDate().compareTo(d1.getCrtnDate())) // Most recent first
                                    .limit(5)
                                    .map(this::convertToDrillDTO) // Assuming a conversion method
                                    .collect(Collectors.toList());

                            return com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO.builder()
                                    .totalNumberOfTimesCompleted((int) totalNumberOfTimesCompleted)
                                    .monthlyAverage(monthlyAverage)
                                    .lastFiveDrills(lastFiveDrills)
                                    .build();
                        }
                ));
    }

    private DrillDTO convertToDrillDTO(Drill drill) {
        // This is a helper method to convert Drill entity to DrillDTO
        // A proper implementation would use a mapper (e.g., MapStruct)
        return FitmateServiceUtil.DrillUtil.buildDTO(drill);
    }
}
