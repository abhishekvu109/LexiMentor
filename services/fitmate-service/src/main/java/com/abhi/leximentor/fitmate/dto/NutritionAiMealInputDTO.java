package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Builder
@Data
public class NutritionAiMealInputDTO {
    private String mealType;
    private String foodName;
    private double servingQty;
    private String servingUnit;
    private String notes;
    private LocalTime entryTime;
}
