package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsPipeline;
import com.abhi.leximentor.fitmate.analytics.calculators.ActivityConsistencyCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.BodyPartWorkoutVolumeCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.CaloriesDurationCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.DashboardSummaryCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.DifficultyRiskMixCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.EquipmentUsageCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.ExerciseAnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.ExerciseLibraryInsightsCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.ExercisePerformanceCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.ExerciseProgressionsCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.MostFrequentExercisesCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.MuscleBodyPartFocusCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.PersonalRecordsCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.RoutineDistributionByTrainingCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.RoutineEfficiencyCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.RoutineStructureCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.TrainingAdherenceCalculator;
import com.abhi.leximentor.fitmate.analytics.calculators.WorkoutTrendsCalculator;
import com.abhi.leximentor.fitmate.dto.ActivityConsistencyDTO;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.CaloriesDurationDTO;
import com.abhi.leximentor.fitmate.dto.DashboardSummaryDTO;
import com.abhi.leximentor.fitmate.dto.DifficultyRiskMixDTO;
import com.abhi.leximentor.fitmate.dto.EquipmentUsageDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseFrequencyDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseLibraryInsightsDTO;
import com.abhi.leximentor.fitmate.dto.ExercisePerformanceDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseProgressionDTO;
import com.abhi.leximentor.fitmate.dto.MuscleBodyPartFocusDTO;
import com.abhi.leximentor.fitmate.dto.PersonalRecordsDTO;
import com.abhi.leximentor.fitmate.dto.RoutineEfficiencyDTO;
import com.abhi.leximentor.fitmate.dto.RoutineStructureDTO;
import com.abhi.leximentor.fitmate.dto.TrainingAdherenceDTO;
import com.abhi.leximentor.fitmate.dto.WorkoutTrendDTO;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final AnalyticsPipeline analyticsPipeline;


    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getOverallAnalytics(String username, Integer rangeDays) {
        log.info("Initiating overall analytics for user: {}", username);
        AnalyticsContext context = buildContext(username, rangeDays);
        return analyticsPipeline.run(context);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, ExerciseAnalyticsDTO> getExerciseAnalytics(String username, Integer rangeDays) {
        log.info("Initiating exercise analytics for user: {}", username);
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(ExerciseAnalyticsCalculator.class));
        return analyticsDTO.getExerciseAnalytics();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(DashboardSummaryCalculator.class));
        return analyticsDTO.getSummary();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutTrendDTO> getWorkoutTrends(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(WorkoutTrendsCalculator.class));
        return analyticsDTO.getWorkoutTrends();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getBodyPartWorkoutVolume(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(BodyPartWorkoutVolumeCalculator.class));
        return analyticsDTO.getBodyPartWorkoutVolume();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseFrequencyDTO> getMostFrequentExercises(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(MostFrequentExercisesCalculator.class));
        return analyticsDTO.getMostFrequentExercises();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getRoutineDistributionByTrainingType(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(RoutineDistributionByTrainingCalculator.class));
        return analyticsDTO.getRoutineDistributionByTrainingType();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ExerciseProgressionDTO>> getExerciseProgressions(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(ExerciseProgressionsCalculator.class));
        return analyticsDTO.getExerciseProgressions();
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineEfficiencyDTO getRoutineEfficiency(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(RoutineEfficiencyCalculator.class));
        return analyticsDTO.getRoutineEfficiency();
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityConsistencyDTO getActivityConsistency(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(ActivityConsistencyCalculator.class));
        return analyticsDTO.getActivityConsistency();
    }

    @Override
    @Transactional(readOnly = true)
    public CaloriesDurationDTO getCaloriesDuration(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(CaloriesDurationCalculator.class));
        return analyticsDTO.getCaloriesDuration();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingAdherenceDTO getTrainingAdherence(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(TrainingAdherenceCalculator.class));
        return analyticsDTO.getTrainingAdherence();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExercisePerformanceDTO> getExercisePerformance(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(ExercisePerformanceCalculator.class));
        return analyticsDTO.getExercisePerformance();
    }

    @Override
    @Transactional(readOnly = true)
    public MuscleBodyPartFocusDTO getMuscleBodyPartFocus(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(MuscleBodyPartFocusCalculator.class));
        return analyticsDTO.getMuscleBodyPartFocus();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentUsageDTO getEquipmentUsage(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(EquipmentUsageCalculator.class));
        return analyticsDTO.getEquipmentUsage();
    }

    @Override
    @Transactional(readOnly = true)
    public DifficultyRiskMixDTO getDifficultyRiskMix(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(DifficultyRiskMixCalculator.class));
        return analyticsDTO.getDifficultyRiskMix();
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineStructureDTO getRoutineStructure(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(RoutineStructureCalculator.class));
        return analyticsDTO.getRoutineStructure();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalRecordsDTO getPersonalRecords(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(PersonalRecordsCalculator.class));
        return analyticsDTO.getPersonalRecords();
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseLibraryInsightsDTO getExerciseLibraryInsights(String username, Integer rangeDays) {
        AnalyticsDTO analyticsDTO = runAnalytics(username, rangeDays, List.of(ExerciseLibraryInsightsCalculator.class));
        return analyticsDTO.getExerciseLibraryInsights();
    }

    private AnalyticsDTO runAnalytics(String username, Integer rangeDays, List<Class<? extends AnalyticsCalculator>> calculatorTypes) {
        AnalyticsContext context = buildContext(username, rangeDays);
        return analyticsPipeline.run(context, calculatorTypes);
    }

    private AnalyticsContext buildContext(String username, Integer rangeDays) {
        List<Routine> userRoutines = loadRoutines(username, rangeDays);
        List<Exercise> allExercises = exerciseRepository.findAll();
        return AnalyticsContext.from(username, userRoutines, allExercises);
    }

    private List<Routine> loadRoutines(String username, Integer rangeDays) {
        if (rangeDays == null) {
            return routineRepository.findByUsername(username);
        }
        int safeDays = Math.max(rangeDays, 1);
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(safeDays - 1L);
        return routineRepository.findByUsernameAndRoutineDateBetween(username, startDate, today);
    }
}
