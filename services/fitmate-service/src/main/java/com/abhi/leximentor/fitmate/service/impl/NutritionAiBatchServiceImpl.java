package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.NutritionAiBatchRequestDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiBatchStatusDTO;
import com.abhi.leximentor.fitmate.service.NutritionAiBatchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionAiBatchServiceImpl implements NutritionAiBatchService {
    private final NutritionAiBatchStore store;
    private final NutritionAiBatchWorker worker;

    @Override
    public NutritionAiBatchStatusDTO submit(NutritionAiBatchRequestDTO request) {
        validateRequest(request);
        String requestId = UUID.randomUUID().toString();
        store.create(requestId, request.getUsername().trim(), request.getMeals().size());
        worker.process(requestId, request);
        return store.get(requestId);
    }

    @Override
    public NutritionAiBatchStatusDTO getStatus(String requestId, String username) {
        if (StringUtils.isBlank(requestId)) {
            throw new IllegalArgumentException("requestId is required");
        }
        NutritionAiBatchStatusDTO status = store.get(requestId);
        if (status == null) {
            throw new IllegalArgumentException("Batch request not found");
        }
        if (StringUtils.isBlank(username) || !status.getUsername().equalsIgnoreCase(username.trim())) {
            throw new IllegalArgumentException("Invalid username for this request");
        }
        return status;
    }

    private void validateRequest(NutritionAiBatchRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("request payload is required");
        }
        if (StringUtils.isBlank(request.getUsername())) {
            throw new IllegalArgumentException("username is required");
        }
        if (CollectionUtils.isEmpty(request.getMeals())) {
            throw new IllegalArgumentException("at least one meal is required");
        }
        if (request.getMeals().size() > 30) {
            throw new IllegalArgumentException("Maximum 30 meals are allowed per request");
        }
        request.getMeals().forEach(meal -> {
            if (meal == null || StringUtils.isBlank(meal.getFoodName())) {
                throw new IllegalArgumentException("Each meal must have foodName");
            }
            if (StringUtils.isBlank(meal.getMealType())) {
                throw new IllegalArgumentException("Each meal must have mealType");
            }
            if (meal.getServingQty() < 0) {
                throw new IllegalArgumentException("servingQty cannot be negative");
            }
        });
    }
}
