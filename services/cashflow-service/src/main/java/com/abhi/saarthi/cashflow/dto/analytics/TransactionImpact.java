package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionImpact {
    private String date;
    private String category;
    private String description;
    private double amount;
}
