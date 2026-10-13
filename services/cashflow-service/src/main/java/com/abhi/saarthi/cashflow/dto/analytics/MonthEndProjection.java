package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthEndProjection {
    private double projectedTotal;
    private double lowerBound;
    private double upperBound;
    private long basisDays;
}
