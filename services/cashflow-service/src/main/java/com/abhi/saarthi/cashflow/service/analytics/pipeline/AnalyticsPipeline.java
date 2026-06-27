package com.abhi.saarthi.cashflow.service.analytics.pipeline;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AnalyticsPipeline {

    private final List<AnalyticsStage> stages;

    public void execute(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        stages.forEach(stage -> stage.process(context, request));
    }
}