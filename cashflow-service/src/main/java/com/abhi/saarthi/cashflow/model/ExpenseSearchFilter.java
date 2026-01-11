package com.abhi.saarthi.cashflow.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class ExpenseSearchFilter {
    private String refId;
    private String uuid;
    private String householdRefId;
    private String owner;
    private double amountFrom;
    private double amountTo;
    private LocalDate expenseDateFrom;
    private LocalDate expenseDateTo;
    private String categoryRefId;
    private String expenseType;
    private String sortBy="expenseDate";
    private String sortDir="desc";
}
