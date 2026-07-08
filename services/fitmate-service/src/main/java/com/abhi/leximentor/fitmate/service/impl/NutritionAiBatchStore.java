package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.NutritionAiBatchStatusDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiMealResultDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class NutritionAiBatchStore {
    private final ConcurrentMap<String, NutritionAiBatchStatusDTO> jobs = new ConcurrentHashMap<>();

    public void create(String requestId, String username, int total) {
        NutritionAiBatchStatusDTO status = NutritionAiBatchStatusDTO.builder()
                .requestId(requestId)
                .username(username)
                .status("QUEUED")
                .total(total)
                .completed(0)
                .failed(0)
                .submittedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .results(new ArrayList<>())
                .build();
        jobs.put(requestId, status);
    }

    public NutritionAiBatchStatusDTO get(String requestId) {
        return jobs.get(requestId);
    }

    public void start(String requestId) {
        NutritionAiBatchStatusDTO current = jobs.get(requestId);
        if (current == null) {
            return;
        }
        synchronized (current) {
            current.setStatus("IN_PROGRESS");
            current.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void appendResult(String requestId, NutritionAiMealResultDTO result, boolean failed) {
        NutritionAiBatchStatusDTO current = jobs.get(requestId);
        if (current == null) {
            return;
        }
        synchronized (current) {
            List<NutritionAiMealResultDTO> existing = current.getResults() == null ? new ArrayList<>() : current.getResults();
            existing.add(result);
            current.setResults(existing);
            current.setCompleted(current.getCompleted() + 1);
            if (failed) {
                current.setFailed(current.getFailed() + 1);
            }
            current.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void complete(String requestId) {
        NutritionAiBatchStatusDTO current = jobs.get(requestId);
        if (current == null) {
            return;
        }
        synchronized (current) {
            if (current.getCompleted() == 0 && current.getFailed() > 0) {
                current.setStatus("FAILED");
            } else if (current.getFailed() > 0) {
                current.setStatus("COMPLETED_WITH_ERRORS");
            } else {
                current.setStatus("COMPLETED");
            }
            current.setUpdatedAt(LocalDateTime.now());
        }
    }
}
