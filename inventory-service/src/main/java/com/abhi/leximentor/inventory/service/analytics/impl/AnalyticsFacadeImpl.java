package com.abhi.leximentor.inventory.service.analytics.impl;

import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTrendsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTypePerformanceDTO;
import com.abhi.leximentor.inventory.dto.analytics.UserPerformanceDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.inventory.service.analytics.AnalyticsFacade;
import com.abhi.leximentor.inventory.service.analytics.engine.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsFacadeImpl implements AnalyticsFacade {
    private final AnalyticsStrategyRegistry strategyRegistry;

    @Override
    public DrillAnalyticsDTO getDrillAnalytics(long drillRefId, int topN) {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL)
                .drillRefId(drillRefId)
                .topN(topN)
                .build();
        return strategyRegistry.execute(AnalyticsType.DRILL, request, DrillAnalyticsDTO.class);
    }

    @Override
    public List<DrillChallengeAnalyticsDTO> getDrillChallengeAnalytics() {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_CHALLENGE)
                .build();
        return strategyRegistry.execute(AnalyticsType.DRILL_CHALLENGE, request, List.class);
    }

    @Override
    public WordAnalyticsDTO getWordAnalyticsOverview() {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_METADATA)
                .build();
        return strategyRegistry.execute(AnalyticsType.WORD_METADATA, request, WordAnalyticsDTO.class);
    }

    @Override
    public List<DrillTypePerformanceDTO> getDrillTypeSummary() {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_TYPE_SUMMARY)
                .build();
        return strategyRegistry.execute(AnalyticsType.DRILL_TYPE_SUMMARY, request, List.class);
    }

    @Override
    public DrillTrendsDTO getDrillTrends(int days, String username) {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_TRENDS)
                .days(days)
                .username(username)
                .build();
        return strategyRegistry.execute(AnalyticsType.DRILL_TRENDS, request, DrillTrendsDTO.class);
    }

    @Override
    public WordDistributionDTO getWordDistribution() {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_DISTRIBUTION)
                .build();
        return strategyRegistry.execute(AnalyticsType.WORD_DISTRIBUTION, request, WordDistributionDTO.class);
    }

    @Override
    public UserPerformanceDTO getUserPerformance(String username, int topN) {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.USER_PERFORMANCE)
                .username(username)
                .topN(topN)
                .build();
        return strategyRegistry.execute(AnalyticsType.USER_PERFORMANCE, request, UserPerformanceDTO.class);
    }

    @Override
    public List<WordDifficultyDTO> getWordDifficultyHeatmap(int topN) {
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_DIFFICULTY)
                .topN(topN)
                .build();
        return strategyRegistry.execute(AnalyticsType.WORD_DIFFICULTY, request, List.class);
    }
}
