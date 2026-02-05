package com.abhi.saarthi.cashflow.model;

import com.abhi.saarthi.cashflow.entities.Expense;
import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Data
public class ExpenseAnalyticsContext {

    private List<Expense> expenses = new ArrayList<>();

    private Map<String, Double> categoryTotals = new HashMap<>();
    private Map<LocalDate, Double> dailyTotals = new HashMap<>();
    private Map<YearMonth, Double> monthlyTotals = new HashMap<>();
    private Map<String, Double> memberTotals = new HashMap<>();

    private List<Metric> metrics = new ArrayList<>();
    private List<Insight> insights = new ArrayList<>();

    // 👇 holds previous-period analytics
    private ExpenseAnalyticsContext comparisonContext;

    public Optional<ExpenseAnalyticsContext> comparison() {
        return Optional.ofNullable(comparisonContext);
    }

    public void addMetric(Metric metric) {
        this.metrics.add(metric);
    }

    public void addInsight(Insight insight) {
        this.insights.add(insight);
    }
}