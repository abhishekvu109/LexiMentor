package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class NutritionAiMealResultDTO {
    private int index;
    private String status;
    private String errorMessage;
    private FoodEntryDTO suggestedEntry;
    private List<String> assumptions;
    private Integer confidence;
}
