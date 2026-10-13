package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeakageCategory {
    private String category;
    private long count;
    private double average;
    private double total;
}
