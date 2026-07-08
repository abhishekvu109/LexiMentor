package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Forecast {
    private String method;
    private long basisDays;
    private double averageDaily;
    private double projectedTotal;
}
