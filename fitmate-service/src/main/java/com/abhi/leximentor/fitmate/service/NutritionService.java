package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.FoodEntryDTO;
import com.abhi.leximentor.fitmate.dto.NutritionDailySummaryDTO;
import com.abhi.leximentor.fitmate.dto.NutritionGoalDTO;
import com.abhi.leximentor.fitmate.dto.NutritionTrendSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface NutritionService {
    NutritionGoalDTO upsertGoal(NutritionGoalDTO goalDTO);

    NutritionGoalDTO getActiveGoal(String username);

    FoodEntryDTO addEntry(FoodEntryDTO foodEntryDTO);

    FoodEntryDTO updateEntry(String username, long refId, FoodEntryDTO foodEntryDTO);

    void deleteEntry(String username, long refId);

    List<FoodEntryDTO> getEntries(String username, LocalDate fromDate, LocalDate toDate, String mealType);

    NutritionDailySummaryDTO getDailySummary(String username, LocalDate date);

    NutritionTrendSummaryDTO getTrends(String username, LocalDate fromDate, LocalDate toDate);
}
