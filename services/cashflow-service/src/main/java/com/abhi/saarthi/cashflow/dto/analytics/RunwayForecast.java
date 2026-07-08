package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunwayForecast {
    private double budgetTotal;
    private double spent;
    private double remaining;
    private double averageDaily;
    private double daysUntilExceeded;
}
