package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.*;

public interface AnalyticsService {
    AnalyticsResult analyze(AnalyticsRequest request);
    AnalyticsCoreResponse analyzeCore(AnalyticsRequest request);
    AnalyticsBehaviorResponse analyzeBehavior(AnalyticsRequest request);
    AnalyticsDiagnosticResponse analyzeDiagnostic(AnalyticsRequest request);
    AnalyticsPlanningResponse analyzePlanning(AnalyticsRequest request);
}
