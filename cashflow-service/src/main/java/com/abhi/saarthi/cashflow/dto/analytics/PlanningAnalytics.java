package com.abhi.saarthi.cashflow.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanningAnalytics {
    private RunwayForecast runwayForecast;
    private List<WhatIfScenario> whatIfScenarios;
    private GoalTracking goalTracking;
    private MonthEndProjection monthEndProjection;
}
