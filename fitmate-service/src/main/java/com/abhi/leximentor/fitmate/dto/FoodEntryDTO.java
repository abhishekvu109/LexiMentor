package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
public class FoodEntryDTO {
    private String refId;
    private String username;
    private LocalDate entryDate;
    private LocalTime entryTime;
    private String mealType;
    private String foodName;
    private double servingQty;
    private String servingUnit;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;
    private double sugar;
    private double sodium;
    private String sourceType;
    private String notes;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
}
