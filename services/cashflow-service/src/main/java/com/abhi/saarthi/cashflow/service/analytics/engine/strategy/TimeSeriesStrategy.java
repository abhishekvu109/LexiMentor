package com.abhi.saarthi.cashflow.service.analytics.engine.strategy;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Metric;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

@Component
public class TimeSeriesStrategy implements AnalyticsStrategy {

    @Override
    public AnalyticsType type() {
        return AnalyticsType.TIME_SERIES;
    }

    @Override
    public void compute(ExpenseAnalyticsContext context) {
        if(MapUtils.isNotEmpty(context.getDailyTotals())){
            context.getMetrics().add(new Metric("DAILY_TOTALS", context.getDailyTotals()));
        }
    }
}
