package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Comparison {
    private double currentTotal;
    private double previousTotal;
    private double change;
    private double percentage;
}
