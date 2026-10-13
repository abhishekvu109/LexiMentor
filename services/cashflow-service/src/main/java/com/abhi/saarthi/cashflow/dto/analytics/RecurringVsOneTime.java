package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecurringVsOneTime {
    private double recurringTotal;
    private double oneTimeTotal;
    private double recurringPercentage;
}
