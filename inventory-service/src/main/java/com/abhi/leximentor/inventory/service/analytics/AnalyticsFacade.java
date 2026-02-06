package com.abhi.leximentor.inventory.service.analytics;

import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTrendsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTypePerformanceDTO;
import com.abhi.leximentor.inventory.dto.analytics.UserPerformanceDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordAnalyticsDTO;

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
