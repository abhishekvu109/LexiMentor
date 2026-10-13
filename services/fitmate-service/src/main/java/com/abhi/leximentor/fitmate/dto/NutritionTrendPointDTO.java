package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class NutritionTrendPointDTO {
    private LocalDate date;
    private double consumedCalories;
    private double consumedProtein;
    private double consumedCarbs;
    private double consumedFat;
    private int totalEntries;
    private boolean withinGoalRange;
}
