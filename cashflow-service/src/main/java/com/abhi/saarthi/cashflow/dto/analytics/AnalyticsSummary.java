package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AnalyticsSummary {
    private LocalDate from;
    private LocalDate to;
    private long days;
    private long transactions;
    private double total;
    private double averagePerDay;
    private double averagePerTransaction;
}
