package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class NutritionTrendSummaryDTO {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private double caloriesTarget;
    private int daysWithEntries;
    private int adherentDays;
    private double adherencePercentage;
    private List<NutritionTrendPointDTO> points;
}
