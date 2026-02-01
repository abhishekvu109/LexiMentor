package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;

public interface AnalyticsService {
    AnalyticsDTO getOverallAnalytics(String username);
    // Potentially add more specific methods later, e.g.,
    // DashboardSummaryDTO getDashboardSummary(String username);
    // List<WorkoutTrendDTO> getWorkoutTrends(String username, Period period);
    // ExerciseAnalyticsDTO getExerciseAnalytics(String username, long exerciseId);
}
