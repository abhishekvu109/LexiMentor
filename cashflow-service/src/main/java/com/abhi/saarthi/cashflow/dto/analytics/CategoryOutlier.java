package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryOutlier {
    private String category;
    private long outlierCount;
    private double mean;
    private double std;
    private double total;
}
