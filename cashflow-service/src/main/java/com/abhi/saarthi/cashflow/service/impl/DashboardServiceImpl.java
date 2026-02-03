package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.dto.DepositDTO;
import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.dto.dashboard.DashboardDTO;
import com.abhi.saarthi.cashflow.dto.dashboard.SpendingTrendDTO;
import com.abhi.saarthi.cashflow.entities.Earning;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.mappers.ExpenseMapper;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;
import com.abhi.saarthi.cashflow.repository.EarningRepository;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.DashboardService;
import com.abhi.saarthi.cashflow.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class DashboardServiceImpl implements DashboardService {
    private final ExpenseRepository expenseRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdService householdService;
    private final EarningRepository earningRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO buildDashboardOverview(String username) {
        List<Earning> earnings = earningRepository.findByUsernameEqualsIgnoreCase(username);
        List<Expense> expenses = expenseRepository.findByOwnerIgnoreCase(username);
        double totalBalance = earnings.stream().mapToDouble(Earning::getAmount).sum()
                - expenses.stream().mapToDouble(Expense::getAmount).sum();
        List<HouseholdDTO> households = householdService.search(HouseholdSearchFilter.builder().user(username).build());
        if (CollectionUtils.isNotEmpty(households)) {
            households.forEach(household -> {
                double totalDeposit = household.getDeposits().stream().mapToDouble(DepositDTO::getAmount).sum();
                double totalExpense = household.getExpenses().stream().mapToDouble(ExpenseDTO::getAmount).sum();
                household.setAvailableBalance(totalDeposit - totalExpense);
                household.setBudgets(Collections.emptyList());
                household.setExpenses(Collections.emptyList());
                household.setDeposits(Collections.emptyList());
            });
        }
        double currentMonthSpending = expenses.stream()
                .filter(expense -> expense.getExpenseDate() != null && expense.getExpenseDate().getMonth() == LocalDate.now().getMonth() && expense.getExpenseDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(Expense::getAmount)
                .sum();

        List<ExpenseDTO> transactionsLast30Days = expenses.stream()
                .filter(expense -> expense.getExpenseDate() != null && expense.getExpenseDate().isBefore(LocalDate.now().minusDays(30)))
                .map(expenseMapper::toDto)
                .toList();
        long todayTransactions = transactionsLast30Days.stream().filter(expenseDTO -> expenseDTO.getExpenseDate() != null && expenseDTO.getExpenseDate().equals(LocalDate.now())).count();
        List<SpendingTrendDTO> spendingTrendDTOS = transactionsLast30Days.stream().map(expenseDTO -> SpendingTrendDTO.builder().amount(expenseDTO.getAmount()).transactionDate(expenseDTO.getExpenseDate()).build()).toList();
        return DashboardDTO.builder()
                .totalBalance(totalBalance)
                .currentMonthlySpending(currentMonthSpending)
                .activeHouseholds(households.size())
                .spendingTrends(spendingTrendDTOS)
                .todayTransactions(todayTransactions)
                .households(households)
                .recentTransactions(transactionsLast30Days.stream().limit(10).toList())
                .build();
    }
}
