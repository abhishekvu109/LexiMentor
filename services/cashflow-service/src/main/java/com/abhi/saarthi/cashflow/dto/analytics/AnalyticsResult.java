package com.abhi.saarthi.cashflow.dto.analytics;

import com.abhi.saarthi.cashflow.model.Insight;
import com.abhi.saarthi.cashflow.model.Metric;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResult {

    private AnalyticsSummary summary;
    private List<TrendPoint> dailyTrend;
    private List<TrendPoint> monthlyTrend;
    private List<BreakdownItem> categories;
    private List<BreakdownItem> members;
    private List<BudgetItem> budgets;
    private Comparison comparison;
    private Forecast forecast;
    private List<Anomaly> anomalies;
    private BehaviorAnalytics behavior;
    private DiagnosticAnalytics diagnostic;
    private PlanningAnalytics planning;
    private List<Metric> metrics;
    private List<Insight> insights;

}
