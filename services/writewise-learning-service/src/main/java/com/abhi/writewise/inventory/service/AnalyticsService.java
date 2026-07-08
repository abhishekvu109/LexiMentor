package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.analytics.WritingAnalyticsDTO;
import com.abhi.writewise.inventory.dto.analytics.InsightDTO;

public interface AnalyticsService {
    WritingAnalyticsDTO getInstantAnalytics();

    InsightDTO generateLlmInsights();
}
