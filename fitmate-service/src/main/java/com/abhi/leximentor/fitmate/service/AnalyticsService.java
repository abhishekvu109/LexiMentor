package com.abhi.leximentor.fitmate.service;

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

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    default AnalyticsDTO getOverallAnalytics(String username) {
        return getOverallAnalytics(username, null);
    }

    AnalyticsDTO getOverallAnalytics(String username, Integer rangeDays);

    default Map<String, ExerciseAnalyticsDTO> getExerciseAnalytics(String username) {
        return getExerciseAnalytics(username, null);
    }

    Map<String, ExerciseAnalyticsDTO> getExerciseAnalytics(String username, Integer rangeDays);

    DashboardSummaryDTO getDashboardSummary(String username, Integer rangeDays);

    List<WorkoutTrendDTO> getWorkoutTrends(String username, Integer rangeDays);

    Map<String, Long> getBodyPartWorkoutVolume(String username, Integer rangeDays);

    List<ExerciseFrequencyDTO> getMostFrequentExercises(String username, Integer rangeDays);

    Map<String, Long> getRoutineDistributionByTrainingType(String username, Integer rangeDays);

    Map<String, List<ExerciseProgressionDTO>> getExerciseProgressions(String username, Integer rangeDays);

    RoutineEfficiencyDTO getRoutineEfficiency(String username, Integer rangeDays);

    ActivityConsistencyDTO getActivityConsistency(String username, Integer rangeDays);

    CaloriesDurationDTO getCaloriesDuration(String username, Integer rangeDays);

    TrainingAdherenceDTO getTrainingAdherence(String username, Integer rangeDays);

    List<ExercisePerformanceDTO> getExercisePerformance(String username, Integer rangeDays);

    MuscleBodyPartFocusDTO getMuscleBodyPartFocus(String username, Integer rangeDays);

    EquipmentUsageDTO getEquipmentUsage(String username, Integer rangeDays);

    DifficultyRiskMixDTO getDifficultyRiskMix(String username, Integer rangeDays);

    RoutineStructureDTO getRoutineStructure(String username, Integer rangeDays);

    PersonalRecordsDTO getPersonalRecords(String username, Integer rangeDays);

    ExerciseLibraryInsightsDTO getExerciseLibraryInsights(String username, Integer rangeDays);
}
