package com.abhi.leximentor.fitmate.analytics;

import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;

public interface AnalyticsCalculator {
    void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder);
}
