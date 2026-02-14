package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class NutritionAiBatchRequestDTO {
    private String username;
    private LocalDate entryDate;
    private List<NutritionAiMealInputDTO> meals;
}
