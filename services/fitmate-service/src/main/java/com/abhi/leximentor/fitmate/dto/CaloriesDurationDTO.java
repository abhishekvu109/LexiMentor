package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class CaloriesDurationDTO {
    private double totalCalories;
    private double averageCaloriesPerRoutine;
    private double totalDurationMinutes;
    private double averageDurationMinutes;
    private RoutinePerformanceDTO bestByCalories;
    private RoutinePerformanceDTO worstByCalories;
    private RoutinePerformanceDTO bestByDuration;
    private RoutinePerformanceDTO worstByDuration;
}
