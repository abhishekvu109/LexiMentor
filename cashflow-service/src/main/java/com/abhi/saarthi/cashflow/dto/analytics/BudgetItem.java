package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetItem {
    private String category;
    private double budget;
    private double actual;
    private double remaining;
    private double utilization;
}
