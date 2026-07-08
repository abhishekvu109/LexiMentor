package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhatIfScenario {
    private String label;
    private double adjustedTotal;
    private double delta;
}
