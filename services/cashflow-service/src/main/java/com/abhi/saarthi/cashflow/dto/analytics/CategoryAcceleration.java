package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryAcceleration {
    private String category;
    private double budget;
    private double actualToDate;
    private double expectedToDate;
    private double projectedMonthEnd;
    private double accelerationRatio;
    private boolean alert;
}
