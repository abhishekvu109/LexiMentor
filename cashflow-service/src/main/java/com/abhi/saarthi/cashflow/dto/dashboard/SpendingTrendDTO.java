package com.abhi.saarthi.cashflow.dto.dashboard;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class SpendingTrendDTO {
    private LocalDate transactionDate;
    private double amount;
}
