package com.abhi.saarthi.cashflow.service.analytics.engine.strategy;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.model.Metric;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

@Component
public class MemberStrategy implements AnalyticsStrategy {

    @Override
    public AnalyticsType type() {
        return AnalyticsType.MEMBER;
    }

    @Override
    public void compute(ExpenseAnalyticsContext context) {
        if (MapUtils.isNotEmpty(context.getMemberTotals())) {
            context.getMetrics().add(new Metric("MEMBER_TOTAL", context.getMemberTotals()));
        }
    }
}
