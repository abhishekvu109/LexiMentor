package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class DashboardSummaryDTO {
    private long totalRoutinesCompleted;
    private double totalWorkoutDurationMinutes;
    private double totalCaloriesBurnt;
    private long totalUniqueExercises;
    // Add other summary metrics like average routine duration, average calories per routine, etc.
}
