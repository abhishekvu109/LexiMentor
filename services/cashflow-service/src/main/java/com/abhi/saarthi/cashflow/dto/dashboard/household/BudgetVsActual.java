package com.abhi.saarthi.cashflow.dto.dashboard.household;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class BudgetVsActual {
    private Double budget;
    private Double actual;
}
