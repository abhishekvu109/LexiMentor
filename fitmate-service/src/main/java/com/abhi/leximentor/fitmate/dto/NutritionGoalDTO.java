package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class NutritionGoalDTO {
    private String refId;
    private String username;
    private double dailyCaloriesTarget;
    private double proteinTarget;
    private double carbTarget;
    private double fatTarget;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
}
