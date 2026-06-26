package com.abhi.saarthi.cashflow.dto;

import com.abhi.saarthi.cashflow.constants.ExpenseType;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private String paymentMode;
    @Builder.Default
    private List<String> items = new ArrayList<>();
}
