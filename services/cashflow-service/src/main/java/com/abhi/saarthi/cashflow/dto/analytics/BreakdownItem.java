package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BreakdownItem {
    private String key;
    private double total;
    private double percentage;
    private long count;
}
