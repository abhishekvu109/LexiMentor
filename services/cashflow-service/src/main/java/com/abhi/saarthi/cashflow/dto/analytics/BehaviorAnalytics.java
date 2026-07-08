package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BehaviorAnalytics {
    private RecurringVsOneTime recurringVsOneTime;
    private List<LeakageCategory> leakageCategories;
    private WeekdayWeekendSpend weekdayWeekendSpend;
    private PaydayEffect paydayEffect;
    private List<MerchantCluster> merchantClusters;
    private VolatilitySummary budgetVolatility;
    private List<CategoryAcceleration> categoryAcceleration;
    private SavingsRate savingsRate;
    private List<MemberRatio> expenseToIncomeByMember;
    private LargeTransactionImpact largeTransactionImpact;
}
