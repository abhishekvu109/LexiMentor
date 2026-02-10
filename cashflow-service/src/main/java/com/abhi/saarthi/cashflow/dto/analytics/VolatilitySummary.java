package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VolatilitySummary {
    private double overallScore;
    private List<CategoryVolatility> categories;
}
