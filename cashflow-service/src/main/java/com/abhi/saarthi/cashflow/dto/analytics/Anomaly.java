package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Anomaly {
    private String date;
    private double total;
    private double zScore;
}
