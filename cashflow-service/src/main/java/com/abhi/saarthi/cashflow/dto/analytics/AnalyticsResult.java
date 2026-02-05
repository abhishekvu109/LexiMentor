package com.abhi.saarthi.cashflow.dto.analytics;

import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
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

    private List<Metric> metrics;
    private List<Insight> insights;

    public static AnalyticsResult from(ExpenseAnalyticsContext context) {
        AnalyticsResult result = new AnalyticsResult();
        result.metrics = context.getMetrics();
        result.insights = context.getInsights();
        return result;
    }

}
