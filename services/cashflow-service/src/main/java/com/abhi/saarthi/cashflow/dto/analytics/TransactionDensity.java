package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDensity {
    private double perDay;
    private double perWeek;
}
