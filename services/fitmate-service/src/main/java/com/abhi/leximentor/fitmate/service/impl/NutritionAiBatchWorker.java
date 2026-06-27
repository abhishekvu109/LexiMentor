package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.FoodEntryDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiBatchRequestDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiMealInputDTO;
import com.abhi.leximentor.fitmate.dto.NutritionAiMealResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionAiBatchWorker {
    private final NutritionAiBatchStore store;
    private final NutritionAiEstimator estimator;

    @Async
    public void process(String requestId, NutritionAiBatchRequestDTO request) {
        store.start(requestId);
        for (int i = 0; i < request.getMeals().size(); i++) {
            NutritionAiMealInputDTO meal = request.getMeals().get(i);
            try {
                NutritionAiEstimator.NutritionEstimate estimate = estimator.estimate(meal);
                FoodEntryDTO suggested = FoodEntryDTO.builder()
                        .username(request.getUsername().trim())
                        .entryDate(request.getEntryDate() == null ? LocalDate.now() : request.getEntryDate())
                        .entryTime(meal.getEntryTime() == null ? LocalTime.now() : meal.getEntryTime())
                        .mealType(normalizeMealType(meal.getMealType()))
                        .foodName(meal.getFoodName())
                        .servingQty(meal.getServingQty())
                        .servingUnit(meal.getServingUnit())
                        .calories(estimate.getCalories())
                        .protein(estimate.getProtein())
                        .carbs(estimate.getCarbs())
                        .fat(estimate.getFat())
                        .fiber(estimate.getFiber())
                        .sugar(estimate.getSugar())
                        .sodium(estimate.getSodium())
                        .sourceType("MANUAL")
                        .notes(meal.getNotes())
                        .build();
                store.appendResult(requestId, NutritionAiMealResultDTO.builder()
                        .index(i)
                        .status("SUCCESS")
                        .suggestedEntry(suggested)
                        .assumptions(estimate.getAssumptions())
                        .confidence(estimate.getConfidence())
                        .build(), false);
            } catch (Exception ex) {
                store.appendResult(requestId, NutritionAiMealResultDTO.builder()
                        .index(i)
                        .status("FAILED")
                        .errorMessage(ex.getMessage())
                        .build(), true);
            }
        }
        store.complete(requestId);
    }

    private String normalizeMealType(String mealType) {
        if (mealType == null) {
            return "OTHER";
        }
        String normalized = mealType.trim().toUpperCase(Locale.ENGLISH);
        return switch (normalized) {
            case "BREAKFAST", "LUNCH", "DINNER", "SNACK", "OTHER" -> normalized;
            default -> "OTHER";
        };
    }
}
