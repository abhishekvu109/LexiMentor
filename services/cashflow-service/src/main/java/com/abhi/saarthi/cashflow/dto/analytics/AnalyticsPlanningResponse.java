package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsPlanningResponse {
    private AnalyticsSummary summary;
    private PlanningAnalytics planning;
}
