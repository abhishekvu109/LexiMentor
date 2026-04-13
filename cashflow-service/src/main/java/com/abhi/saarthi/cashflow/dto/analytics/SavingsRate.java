package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavingsRate {
    private double incomeTotal;
    private double expenseTotal;
    private double savings;
    private double savingsRate;
    private double burnRatePerDay;
}
