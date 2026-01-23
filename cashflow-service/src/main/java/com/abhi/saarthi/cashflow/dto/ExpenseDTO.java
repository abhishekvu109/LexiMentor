package com.abhi.saarthi.cashflow.dto;

import com.abhi.saarthi.cashflow.constants.ExpenseType;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class ExpenseDTO {
    private String refId;
    private String uuid;
    private String householdRefId;
    private String owner;
    private double amount;
    private LocalDate expenseDate;
    private String description;
    private String categoryRefId;
    private ExpenseType type;
    private String expenseFor;
}
