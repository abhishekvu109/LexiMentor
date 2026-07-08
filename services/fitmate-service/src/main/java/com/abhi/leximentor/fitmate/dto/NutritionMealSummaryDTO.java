package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NutritionMealSummaryDTO {
    private String mealType;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
}
