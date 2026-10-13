package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentModeShare {
    private String mode;
    private double total;
    private double percentage;
}
