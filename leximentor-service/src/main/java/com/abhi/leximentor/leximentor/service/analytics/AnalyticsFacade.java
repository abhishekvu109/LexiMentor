package com.abhi.leximentor.leximentor.service.analytics;

import com.abhi.leximentor.leximentor.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.analytics.DrillTrendsDTO;
import com.abhi.leximentor.leximentor.dto.analytics.DrillTypePerformanceDTO;
import com.abhi.leximentor.leximentor.dto.analytics.UserPerformanceDTO;
import com.abhi.leximentor.leximentor.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.leximentor.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.leximentor.dto.analytics.WordAnalyticsDTO;

import java.util.List;

public interface AnalyticsFacade {
    DrillAnalyticsDTO getDrillAnalytics(long drillRefId, int topN);

    List<DrillChallengeAnalyticsDTO> getDrillChallengeAnalytics();

    WordAnalyticsDTO getWordAnalyticsOverview();

    List<DrillTypePerformanceDTO> getDrillTypeSummary();

    DrillTrendsDTO getDrillTrends(int days, String username);

    WordDistributionDTO getWordDistribution();

    UserPerformanceDTO getUserPerformance(String username, int topN);

    List<WordDifficultyDTO> getWordDifficultyHeatmap(int topN);
}
