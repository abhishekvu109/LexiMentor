package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO;

import java.util.Map;

public interface AnalyticsService {
    AnalyticsDTO getOverallAnalytics(String username);
    // Potentially add more specific methods later, e.g.,
    // DashboardSummaryDTO getDashboardSummary(String username);
    // List<WorkoutTrendDTO> getWorkoutTrends(String username, Period period);
     Map<String,ExerciseAnalyticsDTO> getExerciseAnalytics(String username);
}
