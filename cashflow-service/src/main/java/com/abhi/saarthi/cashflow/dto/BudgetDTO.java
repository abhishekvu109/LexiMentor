package com.abhi.saarthi.cashflow.dto;

import com.abhi.saarthi.cashflow.constants.Period;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class BudgetDTO {
    private String uuid;
    private String refId;
    private double amount;
    private Period period;
    private String status;
    private String householdRefId;
    private String categoryRefId;
    private int year;
    private int month;
    private LocalDateTime createdAt;
}
