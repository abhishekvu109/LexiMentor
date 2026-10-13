package com.abhi.saarthi.cashflow.dto.analytics;

import com.abhi.saarthi.cashflow.model.Insight;
import com.abhi.saarthi.cashflow.model.Metric;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalyticsCoreResponse {
    private AnalyticsSummary summary;
    private List<TrendPoint> dailyTrend;
    private List<TrendPoint> monthlyTrend;
    private List<BreakdownItem> categories;
    private List<BreakdownItem> members;
    private List<BudgetItem> budgets;
    private Comparison comparison;
    private Forecast forecast;
    private List<Anomaly> anomalies;
    private List<Metric> metrics;
    private List<Insight> insights;
}
