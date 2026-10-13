package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRatio {
    private String member;
    private double expenseTotal;
    private double incomeTotal;
    private double ratio;
}
