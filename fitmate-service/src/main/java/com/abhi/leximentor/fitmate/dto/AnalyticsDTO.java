package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class AnalyticsDTO {
    private DashboardSummaryDTO summary;
    private List<WorkoutTrendDTO> workoutTrends;
    private Map<String, Long> bodyPartWorkoutVolume; // BodyPart name to total drills
    private List<ExerciseFrequencyDTO> mostFrequentExercises; // New field
    private Map<String, Long> routineDistributionByTrainingType; // New field: Training name to count of routines
    private Map<String, ExerciseAnalyticsDTO> exerciseAnalytics;
    private Map<String, List<ExerciseProgressionDTO>> exerciseProgressions; // New field
    private RoutineEfficiencyDTO routineEfficiency; // New field
    private ActivityConsistencyDTO activityConsistency;
    private CaloriesDurationDTO caloriesDuration;
    private TrainingAdherenceDTO trainingAdherence;
    private List<ExercisePerformanceDTO> exercisePerformance;
    private MuscleBodyPartFocusDTO muscleBodyPartFocus;
    private EquipmentUsageDTO equipmentUsage;
    private DifficultyRiskMixDTO difficultyRiskMix;
    private RoutineStructureDTO routineStructure;
    private PersonalRecordsDTO personalRecords;
    private ExerciseLibraryInsightsDTO exerciseLibraryInsights;
}
