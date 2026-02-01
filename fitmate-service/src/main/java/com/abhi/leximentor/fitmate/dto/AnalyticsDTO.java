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
    private Map<String, ExerciseAnalyticsDTO> exerciseAnalytics; // Exercise name to ExerciseAnalyticsDTO
    // Add more fields as needed for other analytics, e.g., muscle group analytics, routine performance
}
