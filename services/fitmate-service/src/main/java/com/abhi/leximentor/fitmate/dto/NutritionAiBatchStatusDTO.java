package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class NutritionAiBatchStatusDTO {
    private String requestId;
    private String username;
    private String status;
    private int total;
    private int completed;
    private int failed;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private List<NutritionAiMealResultDTO> results;
}
