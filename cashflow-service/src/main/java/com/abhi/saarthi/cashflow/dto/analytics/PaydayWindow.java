package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaydayWindow {
    private int windowDays;
    private double windowTotal;
    private double nonWindowTotal;
    private double windowAverage;
    private double nonWindowAverage;
    private double deltaPercentage;
}
