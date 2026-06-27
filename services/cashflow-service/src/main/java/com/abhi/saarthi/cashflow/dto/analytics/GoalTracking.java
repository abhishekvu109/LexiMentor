package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoalTracking {
    private double goal;
    private double current;
    private double percentage;
}
