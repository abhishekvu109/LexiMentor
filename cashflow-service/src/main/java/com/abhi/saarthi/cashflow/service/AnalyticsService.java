package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsResult;

public interface AnalyticsService {
    AnalyticsResult analyze(AnalyticsRequest request);
}
