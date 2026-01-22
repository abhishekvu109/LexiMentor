package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Currency;
import com.abhi.saarthi.cashflow.dto.DashboardOverviewResponse;
import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.dto.dashboard.BudgetStatus;
import com.abhi.saarthi.cashflow.dto.dashboard.Money;
import com.abhi.saarthi.cashflow.model.ExpenseSearchFilter;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.DashboardService;
import com.abhi.saarthi.cashflow.service.ExpenseService;
import com.abhi.saarthi.cashflow.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DashboardServiceImpl implements DashboardService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;
    private final HouseholdRepository householdRepository;
    private final HouseholdService householdService;

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewResponse buildDashboardOverview(String username) {
        List<HouseholdDTO> households = householdService.search(HouseholdSearchFilter.builder().user(username).build());

        // ──────────────────────────────────────────────
        // 2. Calculate KPI values
        // ──────────────────────────────────────────────
        Money totalBalance = calculateTotalBalance(username);
        BigDecimal balanceChangePercentage = BigDecimal.valueOf(12.5); // ← REPLACE

        Money monthlySpending = calculateMonthlySpending(username);
        BigDecimal spendingChangePercentage = BigDecimal.valueOf(4.3); // ← REPLACE

        int activeHouseholdsCount = households.size();
        int activeHouseholdsChange = 0; // ← REPLACE (e.g. compare to last month)

//        BudgetStatus budgetStatus = calculateBudgetStatus(userId);
//        int budgetPercentage = budgetStatus.percentage;
//        String budgetLabel = budgetStatus.label;
        return null;
    }

    private Money calculateTotalBalance(String username) {
        List<ExpenseDTO> expenses = expenseService.search(ExpenseSearchFilter.builder().owner(username).build());
        Double sum = expenses.stream()
                .map(ExpenseDTO::getAmount)
                .reduce(Double.NaN, Double::sum);
        return Money.of(BigDecimal.valueOf(sum), Currency.INR.name());
    }

    private Money calculateMonthlySpending(String username) {
        LocalDateTime start = LocalDateTime.now().toLocalDate()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime end = start.toLocalDate()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atStartOfDay();
        List<ExpenseDTO> expenses = expenseService
                .search(ExpenseSearchFilter.builder()
                        .owner(username)
                        .expenseDateFrom(start.toLocalDate())
                        .expenseDateTo(end.toLocalDate()).build());
        Double sum = expenses.stream()
                .map(ExpenseDTO::getAmount)
                .reduce(Double.NaN, Double::sum);
        return Money.of(BigDecimal.valueOf(sum), Currency.INR.name());
    }

    private BudgetStatus calculateBudgetStatus(String userId) {
        // Example placeholder – replace with real logic
        // int percent = budgetRepository.calculateUsagePercentThisMonth(userId);
        return new BudgetStatus(72, "On Track");
    }
}
