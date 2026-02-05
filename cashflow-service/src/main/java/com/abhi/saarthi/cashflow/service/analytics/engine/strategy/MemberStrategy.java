package com.abhi.saarthi.cashflow.service.analytics.engine.strategy;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberStrategy implements AnalyticsStrategy {

    @Override
    public AnalyticsType type() {
        return AnalyticsType.MEMBER;
    }

    @Override
    public void compute(ExpenseAnalyticsContext context) {

    }
}
