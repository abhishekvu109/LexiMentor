package com.abhi.saarthi.cashflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@Builder
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
    @JsonProperty(value = "sortBy",defaultValue = "expenseDate")
    private String sortBy="expenseDate";
    @JsonProperty(value = "sortDir",defaultValue = "desc")
    private String sortDir="desc";
}
