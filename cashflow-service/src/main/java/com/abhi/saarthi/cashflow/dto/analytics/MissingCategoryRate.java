package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissingCategoryRate {
    private long missingCount;
    private long totalCount;
    private double percentage;
}
