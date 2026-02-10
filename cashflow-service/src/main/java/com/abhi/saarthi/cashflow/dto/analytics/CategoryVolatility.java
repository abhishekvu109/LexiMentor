package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryVolatility {
    private String category;
    private double mean;
    private double std;
    private double score;
}
