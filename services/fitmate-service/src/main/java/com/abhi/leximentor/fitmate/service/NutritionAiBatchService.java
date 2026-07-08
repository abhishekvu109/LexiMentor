package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.NutritionAiBatchRequestDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiBatchStatusDTO;

public interface NutritionAiBatchService {
    NutritionAiBatchStatusDTO submit(NutritionAiBatchRequestDTO request);

    NutritionAiBatchStatusDTO getStatus(String requestId, String username);
}
