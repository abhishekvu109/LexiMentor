package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantCluster {
    private String cluster;
    private long count;
    private double average;
    private double total;
}
