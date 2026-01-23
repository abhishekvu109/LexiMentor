package com.abhi.saarthi.cashflow.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class HouseholdDTO {
    private String uuid;
    private String refId;
    private String name;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private List<HouseholdMemberDTO> members;
    private List<ExpenseDTO> expenses;
    private List<BudgetDTO> budgets;
    private List<DepositDTO> deposits;
}
