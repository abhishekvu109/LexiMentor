package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class NutritionDailySummaryDTO {
    private String username;
    private LocalDate date;
    private double caloriesTarget;
    private double consumedCalories;
    private double remainingCalories;
    private double consumedProtein;
    private double consumedCarbs;
    private double consumedFat;
    private int totalEntries;
    private List<NutritionMealSummaryDTO> mealSummaries;
}
