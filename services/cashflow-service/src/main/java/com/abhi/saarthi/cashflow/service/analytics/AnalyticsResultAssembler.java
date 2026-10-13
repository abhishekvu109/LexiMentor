package com.abhi.saarthi.cashflow.service.analytics;

import com.abhi.saarthi.cashflow.constants.AnalyticsType;
import com.abhi.saarthi.cashflow.constants.ExpenseType;
import com.abhi.saarthi.cashflow.constants.PaymentMode;
import com.abhi.saarthi.cashflow.dto.analytics.*;
import com.abhi.saarthi.cashflow.entities.Budget;
import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.entities.Household;
import com.abhi.saarthi.cashflow.model.ExpenseAnalyticsContext;
import com.abhi.saarthi.cashflow.repository.BudgetRepository;
import com.abhi.saarthi.cashflow.repository.DepositRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsResultAssembler {

    private final BudgetRepository budgetRepository;
    private final HouseholdRepository householdRepository;
    private final DepositRepository depositRepository;

    public AnalyticsResult assemble(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        double total = context.getTotalAmount();
        long transactions = context.getTransactionCount();
        long days = computeDays(request.getFrom(), request.getTo());
        double averagePerDay = days > 0 ? total / days : 0;
        double averagePerTransaction = transactions > 0 ? total / transactions : 0;

        AnalyticsSummary summary = AnalyticsSummary.builder()
                .from(request.getFrom())
                .to(request.getTo())
                .days(days)
                .transactions(transactions)
                .total(total)
                .averagePerDay(averagePerDay)
                .averagePerTransaction(averagePerTransaction)
                .build();

        List<TrendPoint> dailyTrend = context.getDailyTotals().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> TrendPoint.builder()
                        .period(e.getKey().toString())
                        .total(e.getValue())
                        .build())
                .toList();

        List<TrendPoint> monthlyTrend = context.getMonthlyTotals().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> TrendPoint.builder()
                        .period(e.getKey().toString())
                        .total(e.getValue())
                        .build())
                .toList();

        List<BreakdownItem> categories = buildBreakdown(context.getCategoryTotals(), context.getCategoryCounts(), total);
        List<BreakdownItem> members = buildBreakdown(context.getMemberTotals(), context.getMemberCounts(), total);

        Comparison comparison = buildComparison(context, request);
        Forecast forecast = buildForecast(context, request, days, total);
        List<Anomaly> anomalies = buildAnomalies(context, request);
        List<BudgetItem> budgets = buildBudgets(context, request);
        BehaviorAnalytics behavior = buildBehaviorAnalytics(context, request, days, total);
        DiagnosticAnalytics diagnostic = buildDiagnosticAnalytics(context, request, days);
        PlanningAnalytics planning = buildPlanningAnalytics(context, request, days);

        return AnalyticsResult.builder()
                .summary(summary)
                .dailyTrend(dailyTrend)
                .monthlyTrend(monthlyTrend)
                .categories(categories)
                .members(members)
                .comparison(comparison)
                .forecast(forecast)
                .anomalies(anomalies)
                .budgets(budgets)
                .behavior(behavior)
                .diagnostic(diagnostic)
                .planning(planning)
                .metrics(context.getMetrics())
                .insights(context.getInsights())
                .build();
    }

    private long computeDays(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        return ChronoUnit.DAYS.between(from, to) + 1;
    }

    private List<BreakdownItem> buildBreakdown(Map<String, Double> totals, Map<String, Long> counts, double overallTotal) {
        if (totals == null || totals.isEmpty()) return List.of();
        return totals.entrySet().stream()
                .map(entry -> {
                    double value = entry.getValue();
                    double percentage = overallTotal > 0 ? (value / overallTotal) * 100 : 0;
                    long count = counts.getOrDefault(entry.getKey(), 0L);
                    return BreakdownItem.builder()
                            .key(entry.getKey())
                            .total(value)
                            .percentage(percentage)
                            .count(count)
                            .build();
                })
                .sorted(Comparator.comparingDouble(BreakdownItem::getTotal).reversed())
                .toList();
    }

    private Comparison buildComparison(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        if (!request.hasComparison() || context.comparison().isEmpty()) return null;
        double currentTotal = context.getTotalAmount();
        double previousTotal = context.comparison().get().getTotalAmount();
        double change = currentTotal - previousTotal;
        double percentage = previousTotal != 0 ? (change / previousTotal) * 100 : 0;
        return Comparison.builder()
                .currentTotal(currentTotal)
                .previousTotal(previousTotal)
                .change(change)
                .percentage(percentage)
                .build();
    }

    private Forecast buildForecast(ExpenseAnalyticsContext context, AnalyticsRequest request, long days, double total) {
        if (request.getAnalyticsTypes() == null || !request.requires(AnalyticsType.FORECAST)) return null;
        long basisDays = context.getDailyTotals().size();
        if (basisDays == 0) return null;
        double averageDaily = total / basisDays;
        long projectionDays = days > 0 ? days : basisDays;
        double projectedTotal = averageDaily * projectionDays;
        return Forecast.builder()
                .method("AVG_DAILY")
                .basisDays(basisDays)
                .averageDaily(averageDaily)
                .projectedTotal(projectedTotal)
                .build();
    }

    private List<Anomaly> buildAnomalies(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        if (request.getAnalyticsTypes() == null || !request.requires(AnalyticsType.ANOMALY)) return List.of();
        if (context.getDailyTotals().isEmpty()) return List.of();

        List<Double> values = new ArrayList<>(context.getDailyTotals().values());
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
        double std = Math.sqrt(variance);
        if (std == 0) return List.of();

        return context.getDailyTotals().entrySet().stream()
                .map(entry -> {
                    double zScore = (entry.getValue() - mean) / std;
                    return new AbstractMap.SimpleEntry<>(entry, zScore);
                })
                .filter(entry -> entry.getValue() >= 2.0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(entry -> Anomaly.builder()
                        .date(entry.getKey().getKey().toString())
                        .total(entry.getKey().getValue())
                        .zScore(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<BudgetItem> buildBudgets(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        if (request.getAnalyticsTypes() == null || !request.requires(AnalyticsType.BUDGET)) return List.of();
        if (request.getHouseholdRefId() == null) return List.of();

        Optional<Household> household = householdRepository.findByRefId(Long.parseLong(request.getHouseholdRefId()));
        if (household.isEmpty()) return List.of();

        List<Budget> budgets = budgetRepository.findByHousehold(household.get());
        if (budgets.isEmpty()) return List.of();

        YearMonth fromMonth = request.getFrom() != null ? YearMonth.from(request.getFrom()) : null;
        YearMonth toMonth = request.getTo() != null ? YearMonth.from(request.getTo()) : null;

        Map<String, Double> budgetTotals = new HashMap<>();
        budgets.forEach(budget -> {
            YearMonth budgetMonth = resolveBudgetMonth(budget);
            if (budgetMonth != null && fromMonth != null && toMonth != null) {
                if (budgetMonth.isBefore(fromMonth) || budgetMonth.isAfter(toMonth)) return;
            }
            String category = budget.getCategory() != null ? budget.getCategory().getName() : "UNCATEGORIZED";
            budgetTotals.merge(category, budget.getAmount(), Double::sum);
        });

        if (budgetTotals.isEmpty()) return List.of();

        return budgetTotals.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    double budget = entry.getValue();
                    double actual = context.getCategoryTotals().getOrDefault(category, 0.0);
                    double remaining = budget - actual;
                    double utilization = budget > 0 ? (actual / budget) * 100 : 0;
                    return BudgetItem.builder()
                            .category(category)
                            .budget(budget)
                            .actual(actual)
                            .remaining(remaining)
                            .utilization(utilization)
                            .build();
                })
                .sorted(Comparator.comparingDouble(BudgetItem::getUtilization).reversed())
                .toList();
    }

    private YearMonth resolveBudgetMonth(Budget budget) {
        if (budget.getBudgetDate() != null) {
            return YearMonth.from(budget.getBudgetDate());
        }
        if (budget.getYear() != null && budget.getMonth() != null) {
            return YearMonth.of(budget.getYear(), budget.getMonth());
        }
        return null;
    }

    private BehaviorAnalytics buildBehaviorAnalytics(ExpenseAnalyticsContext context, AnalyticsRequest request, long days, double total) {
        RecurringVsOneTime recurringVsOneTime = buildRecurringVsOneTime(context);
        List<LeakageCategory> leakageCategories = buildLeakageCategories(context);
        WeekdayWeekendSpend weekdayWeekendSpend = buildWeekdayWeekendSpend(context);
        PaydayEffect paydayEffect = buildPaydayEffect(request, context);
        List<MerchantCluster> merchantClusters = buildMerchantClusters(context);
        VolatilitySummary budgetVolatility = buildBudgetVolatility(context);
        List<CategoryAcceleration> categoryAcceleration = buildCategoryAcceleration(context, request);
        SavingsRate savingsRate = buildSavingsRate(context, request, days);
        List<MemberRatio> memberRatios = buildMemberRatios(context, request);
        LargeTransactionImpact largeTransactionImpact = buildLargeTransactionImpact(context, total);

        return BehaviorAnalytics.builder()
                .recurringVsOneTime(recurringVsOneTime)
                .leakageCategories(leakageCategories)
                .weekdayWeekendSpend(weekdayWeekendSpend)
                .paydayEffect(paydayEffect)
                .merchantClusters(merchantClusters)
                .budgetVolatility(budgetVolatility)
                .categoryAcceleration(categoryAcceleration)
                .savingsRate(savingsRate)
                .expenseToIncomeByMember(memberRatios)
                .largeTransactionImpact(largeTransactionImpact)
                .build();
    }

    private DiagnosticAnalytics buildDiagnosticAnalytics(ExpenseAnalyticsContext context, AnalyticsRequest request, long days) {
        MissingCategoryRate missingCategoryRate = buildMissingCategoryRate(context);
        TransactionDensity transactionDensity = buildTransactionDensity(context, days);
        List<CategoryOutlier> categoryOutliers = buildCategoryOutliers(context);
        PeakSpendDay peakSpendDay = buildPeakSpendDay(context);
        List<PaymentModeShare> cashVsCard = buildPaymentModeShares(context);

        return DiagnosticAnalytics.builder()
                .missingCategoryRate(missingCategoryRate)
                .transactionDensity(transactionDensity)
                .categoryOutliers(categoryOutliers)
                .peakSpendDay(peakSpendDay)
                .cashVsCard(cashVsCard)
                .build();
    }

    private PlanningAnalytics buildPlanningAnalytics(ExpenseAnalyticsContext context, AnalyticsRequest request, long days) {
        RunwayForecast runwayForecast = buildRunwayForecast(context, request, days);
        List<WhatIfScenario> whatIfScenarios = buildWhatIfScenarios(context);
        GoalTracking goalTracking = buildGoalTracking(context, request);
        MonthEndProjection monthEndProjection = buildMonthEndProjection(context, request);

        return PlanningAnalytics.builder()
                .runwayForecast(runwayForecast)
                .whatIfScenarios(whatIfScenarios)
                .goalTracking(goalTracking)
                .monthEndProjection(monthEndProjection)
                .build();
    }

    private RecurringVsOneTime buildRecurringVsOneTime(ExpenseAnalyticsContext context) {
        double recurringTotal = context.getExpenses().stream()
                .filter(e -> e.getType() == ExpenseType.RECURRING)
                .mapToDouble(Expense::getAmount)
                .sum();
        double oneTimeTotal = context.getExpenses().stream()
                .filter(e -> e.getType() == ExpenseType.ONE_TIME)
                .mapToDouble(Expense::getAmount)
                .sum();
        double total = recurringTotal + oneTimeTotal;
        double recurringPercentage = total > 0 ? (recurringTotal / total) * 100 : 0;
        return RecurringVsOneTime.builder()
                .recurringTotal(recurringTotal)
                .oneTimeTotal(oneTimeTotal)
                .recurringPercentage(recurringPercentage)
                .build();
    }

    private List<LeakageCategory> buildLeakageCategories(ExpenseAnalyticsContext context) {
        Map<String, List<Expense>> byCategory = context.getExpenses().stream()
                .collect(Collectors.groupingBy(e -> e.getCategory() != null ? e.getCategory().getName() : "UNCATEGORIZED"));

        double overallAverage = context.getExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0);

        return byCategory.entrySet().stream()
                .map(entry -> {
                    List<Expense> expenses = entry.getValue();
                    long count = expenses.size();
                    double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
                    double average = count > 0 ? total / count : 0;
                    return LeakageCategory.builder()
                            .category(entry.getKey())
                            .count(count)
                            .average(average)
                            .total(total)
                            .build();
                })
                .filter(item -> item.getCount() >= 3 && item.getAverage() <= overallAverage)
                .sorted(Comparator.comparingDouble(LeakageCategory::getTotal).reversed())
                .limit(5)
                .toList();
    }

    private WeekdayWeekendSpend buildWeekdayWeekendSpend(ExpenseAnalyticsContext context) {
        double weekdayTotal = 0;
        double weekendTotal = 0;
        long weekdayDays = 0;
        long weekendDays = 0;

        for (Map.Entry<LocalDate, Double> entry : context.getDailyTotals().entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey().getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                weekendTotal += entry.getValue();
                weekendDays += 1;
            } else {
                weekdayTotal += entry.getValue();
                weekdayDays += 1;
            }
        }

        double weekdayAverage = weekdayDays > 0 ? weekdayTotal / weekdayDays : 0;
        double weekendAverage = weekendDays > 0 ? weekendTotal / weekendDays : 0;

        return WeekdayWeekendSpend.builder()
                .weekdayTotal(weekdayTotal)
                .weekendTotal(weekendTotal)
                .weekdayAverage(weekdayAverage)
                .weekendAverage(weekendAverage)
                .build();
    }

    private PaydayEffect buildPaydayEffect(AnalyticsRequest request, ExpenseAnalyticsContext context) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return null;
        List<Deposit> deposits = depositRepository.findByHousehold(household.get()).stream()
                .filter(d -> isBetween(d.getDepositDate(), request.getFrom(), request.getTo()))
                .toList();
        if (deposits.isEmpty()) return null;

        List<PaydayWindow> windows = List.of(
                buildPaydayWindow(3, deposits, context),
                buildPaydayWindow(5, deposits, context)
        );
        return PaydayEffect.builder()
                .windows(windows)
                .build();
    }

    private PaydayWindow buildPaydayWindow(int windowDays, List<Deposit> deposits, ExpenseAnalyticsContext context) {
        Set<LocalDate> windowDates = new HashSet<>();
        for (Deposit deposit : deposits) {
            LocalDate start = deposit.getDepositDate();
            if (start == null) continue;
            for (int i = 0; i < windowDays; i++) {
                windowDates.add(start.plusDays(i));
            }
        }

        double windowTotal = 0;
        double nonWindowTotal = 0;
        long windowCount = 0;
        long nonWindowCount = 0;

        for (Map.Entry<LocalDate, Double> entry : context.getDailyTotals().entrySet()) {
            if (windowDates.contains(entry.getKey())) {
                windowTotal += entry.getValue();
                windowCount += 1;
            } else {
                nonWindowTotal += entry.getValue();
                nonWindowCount += 1;
            }
        }

        double windowAverage = windowCount > 0 ? windowTotal / windowCount : 0;
        double nonWindowAverage = nonWindowCount > 0 ? nonWindowTotal / nonWindowCount : 0;
        double deltaPercentage = nonWindowAverage > 0 ? ((windowAverage - nonWindowAverage) / nonWindowAverage) * 100 : 0;

        return PaydayWindow.builder()
                .windowDays(windowDays)
                .windowTotal(windowTotal)
                .nonWindowTotal(nonWindowTotal)
                .windowAverage(windowAverage)
                .nonWindowAverage(nonWindowAverage)
                .deltaPercentage(deltaPercentage)
                .build();
    }

    private List<MerchantCluster> buildMerchantClusters(ExpenseAnalyticsContext context) {
        Map<String, List<Expense>> clusters = context.getExpenses().stream()
                .collect(Collectors.groupingBy(e -> normalizeDescription(e.getDescription())));

        return clusters.entrySet().stream()
                .map(entry -> {
                    List<Expense> expenses = entry.getValue();
                    long count = expenses.size();
                    double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
                    double average = count > 0 ? total / count : 0;
                    return MerchantCluster.builder()
                            .cluster(entry.getKey())
                            .count(count)
                            .average(average)
                            .total(total)
                            .build();
                })
                .filter(item -> item.getCount() >= 2)
                .sorted(Comparator.comparingDouble(MerchantCluster::getTotal).reversed())
                .limit(10)
                .toList();
    }

    private VolatilitySummary buildBudgetVolatility(ExpenseAnalyticsContext context) {
        Map<String, Map<YearMonth, Double>> categoryMonthly = new HashMap<>();
        for (Expense expense : context.getExpenses()) {
            String category = expense.getCategory() != null ? expense.getCategory().getName() : "UNCATEGORIZED";
            YearMonth month = YearMonth.from(expense.getExpenseDate());
            categoryMonthly.computeIfAbsent(category, key -> new HashMap<>())
                    .merge(month, expense.getAmount(), Double::sum);
        }

        List<CategoryVolatility> categories = categoryMonthly.entrySet().stream()
                .map(entry -> {
                    List<Double> values = new ArrayList<>(entry.getValue().values());
                    if (values.size() < 2) return null;
                    double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
                    double std = Math.sqrt(variance);
                    double score = mean > 0 ? std / mean : 0;
                    return CategoryVolatility.builder()
                            .category(entry.getKey())
                            .mean(mean)
                            .std(std)
                            .score(score)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(CategoryVolatility::getScore).reversed())
                .toList();

        double overallScore = categories.isEmpty() ? 0 :
                categories.stream().mapToDouble(CategoryVolatility::getScore).average().orElse(0);

        return VolatilitySummary.builder()
                .overallScore(overallScore)
                .categories(categories)
                .build();
    }

    private List<CategoryAcceleration> buildCategoryAcceleration(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return List.of();

        List<Budget> budgets = budgetRepository.findByHousehold(household.get());
        if (budgets.isEmpty()) return List.of();

        YearMonth targetMonth = request.getTo() != null ? YearMonth.from(request.getTo()) : null;
        if (targetMonth == null) return List.of();

        int daysInMonth = targetMonth.lengthOfMonth();
        int elapsedDays = request.getTo().getDayOfMonth();

        Map<String, Double> budgetTotals = new HashMap<>();
        for (Budget budget : budgets) {
            YearMonth budgetMonth = resolveBudgetMonth(budget);
            if (budgetMonth == null || !budgetMonth.equals(targetMonth)) continue;
            String category = budget.getCategory() != null ? budget.getCategory().getName() : "UNCATEGORIZED";
            budgetTotals.merge(category, budget.getAmount(), Double::sum);
        }

        if (budgetTotals.isEmpty()) return List.of();

        Map<String, Double> actuals = context.getExpenses().stream()
                .filter(e -> YearMonth.from(e.getExpenseDate()).equals(targetMonth))
                .collect(Collectors.groupingBy(e -> e.getCategory() != null ? e.getCategory().getName() : "UNCATEGORIZED",
                        Collectors.summingDouble(Expense::getAmount)));

        return budgetTotals.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    double budget = entry.getValue();
                    double actualToDate = actuals.getOrDefault(category, 0.0);
                    double expectedToDate = budget * ((double) elapsedDays / daysInMonth);
                    double projectedMonthEnd = elapsedDays > 0 ? (actualToDate / elapsedDays) * daysInMonth : 0;
                    double accelerationRatio = expectedToDate > 0 ? actualToDate / expectedToDate : 0;
                    boolean alert = projectedMonthEnd > budget || accelerationRatio >= 1.1;
                    return CategoryAcceleration.builder()
                            .category(category)
                            .budget(budget)
                            .actualToDate(actualToDate)
                            .expectedToDate(expectedToDate)
                            .projectedMonthEnd(projectedMonthEnd)
                            .accelerationRatio(accelerationRatio)
                            .alert(alert)
                            .build();
                })
                .sorted(Comparator.comparingDouble(CategoryAcceleration::getAccelerationRatio).reversed())
                .toList();
    }

    private SavingsRate buildSavingsRate(ExpenseAnalyticsContext context, AnalyticsRequest request, long days) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return null;

        double incomeTotal = depositRepository.findByHousehold(household.get()).stream()
                .filter(d -> isBetween(d.getDepositDate(), request.getFrom(), request.getTo()))
                .mapToDouble(Deposit::getAmount)
                .sum();
        if (incomeTotal <= 0) return null;

        double expenseTotal = context.getTotalAmount();
        double savings = incomeTotal - expenseTotal;
        double savingsRate = incomeTotal > 0 ? (savings / incomeTotal) * 100 : 0;
        double burnRatePerDay = days > 0 ? expenseTotal / days : 0;

        return SavingsRate.builder()
                .incomeTotal(incomeTotal)
                .expenseTotal(expenseTotal)
                .savings(savings)
                .savingsRate(savingsRate)
                .burnRatePerDay(burnRatePerDay)
                .build();
    }

    private List<MemberRatio> buildMemberRatios(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return List.of();

        Map<String, Double> incomeByUser = depositRepository.findByHousehold(household.get()).stream()
                .filter(d -> isBetween(d.getDepositDate(), request.getFrom(), request.getTo()))
                .filter(d -> d.getUsername() != null)
                .collect(Collectors.groupingBy(d -> d.getUsername().toLowerCase(), Collectors.summingDouble(Deposit::getAmount)));

        if (incomeByUser.isEmpty()) return List.of();

        return context.getMemberTotals().entrySet().stream()
                .map(entry -> {
                    String member = entry.getKey();
                    double expenseTotal = entry.getValue();
                    double incomeTotal = incomeByUser.getOrDefault(member.toLowerCase(), 0.0);
                    double ratio = incomeTotal > 0 ? expenseTotal / incomeTotal : 0;
                    return MemberRatio.builder()
                            .member(member)
                            .expenseTotal(expenseTotal)
                            .incomeTotal(incomeTotal)
                            .ratio(ratio)
                            .build();
                })
                .sorted(Comparator.comparingDouble(MemberRatio::getRatio).reversed())
                .toList();
    }

    private LargeTransactionImpact buildLargeTransactionImpact(ExpenseAnalyticsContext context, double total) {
        List<TransactionImpact> topTransactions = context.getExpenses().stream()
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed())
                .limit(5)
                .map(expense -> TransactionImpact.builder()
                        .date(expense.getExpenseDate() != null ? expense.getExpenseDate().toString() : null)
                        .category(expense.getCategory() != null ? expense.getCategory().getName() : "UNCATEGORIZED")
                        .description(expense.getDescription())
                        .amount(expense.getAmount())
                        .build())
                .toList();

        double topTotal = topTransactions.stream().mapToDouble(TransactionImpact::getAmount).sum();
        double topPercentage = total > 0 ? (topTotal / total) * 100 : 0;

        return LargeTransactionImpact.builder()
                .topTransactions(topTransactions)
                .topTotal(topTotal)
                .topPercentage(topPercentage)
                .build();
    }

    private MissingCategoryRate buildMissingCategoryRate(ExpenseAnalyticsContext context) {
        long totalCount = context.getExpenses().size();
        long missingCount = context.getExpenses().stream()
                .filter(e -> e.getCategory() == null || "UNCATEGORIZED".equalsIgnoreCase(e.getCategory().getName()))
                .count();
        double percentage = totalCount > 0 ? ((double) missingCount / totalCount) * 100 : 0;

        return MissingCategoryRate.builder()
                .missingCount(missingCount)
                .totalCount(totalCount)
                .percentage(percentage)
                .build();
    }

    private TransactionDensity buildTransactionDensity(ExpenseAnalyticsContext context, long days) {
        double perDay = days > 0 ? ((double) context.getTransactionCount() / days) : 0;
        double perWeek = days > 0 ? ((double) context.getTransactionCount() / (days / 7.0)) : 0;
        return TransactionDensity.builder()
                .perDay(perDay)
                .perWeek(perWeek)
                .build();
    }

    private List<CategoryOutlier> buildCategoryOutliers(ExpenseAnalyticsContext context) {
        Map<String, List<Expense>> byCategory = context.getExpenses().stream()
                .collect(Collectors.groupingBy(e -> e.getCategory() != null ? e.getCategory().getName() : "UNCATEGORIZED"));

        return byCategory.entrySet().stream()
                .map(entry -> {
                    List<Expense> expenses = entry.getValue();
                    if (expenses.size() < 5) return null;
                    List<Double> values = expenses.stream().map(Expense::getAmount).toList();
                    double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
                    double std = Math.sqrt(variance);
                    long outlierCount = values.stream().filter(v -> v > mean + (2 * std)).count();
                    double total = values.stream().mapToDouble(Double::doubleValue).sum();
                    return CategoryOutlier.builder()
                            .category(entry.getKey())
                            .outlierCount(outlierCount)
                            .mean(mean)
                            .std(std)
                            .total(total)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(CategoryOutlier::getOutlierCount).reversed())
                .toList();
    }

    private PeakSpendDay buildPeakSpendDay(ExpenseAnalyticsContext context) {
        return context.getDailyTotals().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> PeakSpendDay.builder()
                        .date(entry.getKey().toString())
                        .total(entry.getValue())
                        .build())
                .orElse(null);
    }

    private List<PaymentModeShare> buildPaymentModeShares(ExpenseAnalyticsContext context) {
        Map<String, Double> totals = new HashMap<>();
        for (Expense expense : context.getExpenses()) {
            PaymentMode mode = expense.getPaymentMode();
            String key = mode != null ? mode.name() : "UNKNOWN";
            totals.merge(key, expense.getAmount(), Double::sum);
        }
        double total = totals.values().stream().mapToDouble(Double::doubleValue).sum();
        return totals.entrySet().stream()
                .map(entry -> PaymentModeShare.builder()
                        .mode(entry.getKey())
                        .total(entry.getValue())
                        .percentage(total > 0 ? (entry.getValue() / total) * 100 : 0)
                        .build())
                .sorted(Comparator.comparingDouble(PaymentModeShare::getTotal).reversed())
                .toList();
    }

    private RunwayForecast buildRunwayForecast(ExpenseAnalyticsContext context, AnalyticsRequest request, long days) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return null;
        YearMonth targetMonth = request.getTo() != null ? YearMonth.from(request.getTo()) : null;
        if (targetMonth == null) return null;

        double budgetTotal = budgetRepository.findByHousehold(household.get()).stream()
                .filter(b -> {
                    YearMonth budgetMonth = resolveBudgetMonth(b);
                    return budgetMonth != null && budgetMonth.equals(targetMonth);
                })
                .mapToDouble(Budget::getAmount)
                .sum();
        if (budgetTotal <= 0) return null;

        double spent = context.getExpenses().stream()
                .filter(e -> YearMonth.from(e.getExpenseDate()).equals(targetMonth))
                .mapToDouble(Expense::getAmount)
                .sum();

        int elapsedDays = request.getTo().getDayOfMonth();
        double remaining = budgetTotal - spent;
        double averageDaily = elapsedDays > 0 ? spent / elapsedDays : 0;
        double daysUntilExceeded = averageDaily > 0 ? remaining / averageDaily : 0;

        return RunwayForecast.builder()
                .budgetTotal(budgetTotal)
                .spent(spent)
                .remaining(remaining)
                .averageDaily(averageDaily)
                .daysUntilExceeded(daysUntilExceeded)
                .build();
    }

    private List<WhatIfScenario> buildWhatIfScenarios(ExpenseAnalyticsContext context) {
        double total = context.getTotalAmount();
        List<Map.Entry<String, Double>> topCategories = context.getCategoryTotals().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .toList();

        List<WhatIfScenario> scenarios = new ArrayList<>();
        for (Map.Entry<String, Double> entry : topCategories) {
            double cut = entry.getValue() * 0.2;
            scenarios.add(WhatIfScenario.builder()
                    .label("Cut " + entry.getKey() + " by 20%")
                    .adjustedTotal(total - cut)
                    .delta(-cut)
                    .build());
        }
        return scenarios;
    }

    private GoalTracking buildGoalTracking(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        Optional<Household> household = resolveHousehold(request);
        if (household.isEmpty()) return null;
        YearMonth targetMonth = request.getTo() != null ? YearMonth.from(request.getTo()) : null;
        if (targetMonth == null) return null;

        double goal = budgetRepository.findByHousehold(household.get()).stream()
                .filter(b -> {
                    YearMonth budgetMonth = resolveBudgetMonth(b);
                    return budgetMonth != null && budgetMonth.equals(targetMonth);
                })
                .mapToDouble(Budget::getAmount)
                .sum();
        if (goal <= 0) return null;

        double current = context.getExpenses().stream()
                .filter(e -> YearMonth.from(e.getExpenseDate()).equals(targetMonth))
                .mapToDouble(Expense::getAmount)
                .sum();
        double percentage = goal > 0 ? (current / goal) * 100 : 0;

        return GoalTracking.builder()
                .goal(goal)
                .current(current)
                .percentage(percentage)
                .build();
    }

    private MonthEndProjection buildMonthEndProjection(ExpenseAnalyticsContext context, AnalyticsRequest request) {
        YearMonth targetMonth = request.getTo() != null ? YearMonth.from(request.getTo()) : null;
        if (targetMonth == null) return null;

        List<Double> dailyValues = context.getDailyTotals().entrySet().stream()
                .filter(entry -> YearMonth.from(entry.getKey()).equals(targetMonth))
                .map(Map.Entry::getValue)
                .toList();

        if (dailyValues.isEmpty()) return null;
        double totalSoFar = dailyValues.stream().mapToDouble(Double::doubleValue).sum();
        int elapsedDays = request.getTo().getDayOfMonth();
        int daysInMonth = targetMonth.lengthOfMonth();
        double projectedTotal = elapsedDays > 0 ? (totalSoFar / elapsedDays) * daysInMonth : totalSoFar;

        double mean = dailyValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = dailyValues.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
        double std = Math.sqrt(variance);
        double band = std * Math.sqrt(daysInMonth);

        return MonthEndProjection.builder()
                .projectedTotal(projectedTotal)
                .lowerBound(Math.max(0, projectedTotal - band))
                .upperBound(projectedTotal + band)
                .basisDays(dailyValues.size())
                .build();
    }

    private Optional<Household> resolveHousehold(AnalyticsRequest request) {
        if (request.getHouseholdRefId() == null) return Optional.empty();
        return householdRepository.findByRefId(Long.parseLong(request.getHouseholdRefId()));
    }

    private boolean isBetween(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null || from == null || to == null) return false;
        return !date.isBefore(from) && !date.isAfter(to);
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) return "UNKNOWN";
        String normalized = description.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\d", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.isBlank()) return "UNKNOWN";
        List<String> stopwords = List.of("payment", "txn", "transaction", "online", "pos", "card", "upi");
        List<String> parts = Arrays.stream(normalized.split(" "))
                .filter(p -> !stopwords.contains(p))
                .toList();
        if (parts.isEmpty()) return "UNKNOWN";
        return String.join(" ", parts);
    }
}
