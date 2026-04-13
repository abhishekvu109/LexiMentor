package com.abhi.saarthi.cashflow.dto.dashboard;

import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class DashboardDTO {
    private double totalBalance;
    private double currentMonthlySpending;
    private int activeHouseholds;
    private long todayTransactions;
    private List<HouseholdDTO> households;
    private List<SpendingTrendDTO> spendingTrends;
    private List<ExpenseDTO> recentTransactions;
}




