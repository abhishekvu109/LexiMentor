package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeekdayWeekendSpend {
    private double weekdayTotal;
    private double weekendTotal;
    private double weekdayAverage;
    private double weekendAverage;
}
