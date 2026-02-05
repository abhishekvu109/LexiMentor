package com.abhi.saarthi.cashflow.service.analytics.insight;

import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.service.analytics.insight.generator.InsightGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InsightEngine {

    private final List<InsightGenerator> generators;
    public void generate(ExpenseAnalyticsContext context) {
        generators.stream()
                .flatMap(g -> g.generate(context).stream())
                .forEach(context.getInsights()::add);
    }
}