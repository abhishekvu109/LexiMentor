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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsFacadeImpl implements AnalyticsFacade {
    private final AnalyticsStrategyRegistry strategyRegistry;

    @Override
    public DrillAnalyticsDTO getDrillAnalytics(long drillRefId, int topN) {
        log.info("Drill analytics requested. drillRefId={}, topN={}", drillRefId, topN);
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL)
                .drillRefId(drillRefId)
                .topN(topN)
                .build();
        DrillAnalyticsDTO response = strategyRegistry.execute(AnalyticsType.DRILL, request, DrillAnalyticsDTO.class);
        log.info("Drill analytics completed. drillRefId={}", drillRefId);
        return response;
    }

    @Override
    public List<DrillChallengeAnalyticsDTO> getDrillChallengeAnalytics() {
        log.info("Drill challenge analytics requested");
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_CHALLENGE)
                .build();
        List<DrillChallengeAnalyticsDTO> response = strategyRegistry.executeList(AnalyticsType.DRILL_CHALLENGE, request, DrillChallengeAnalyticsDTO.class);
        log.info("Drill challenge analytics completed. count={}", response.size());
        return response;
    }

    @Override
    public WordAnalyticsDTO getWordAnalyticsOverview() {
        log.info("Word analytics overview requested");
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_METADATA)
                .build();
        WordAnalyticsDTO response = strategyRegistry.execute(AnalyticsType.WORD_METADATA, request, WordAnalyticsDTO.class);
        log.info("Word analytics overview completed");
        return response;
    }

    @Override
    public List<DrillTypePerformanceDTO> getDrillTypeSummary() {
        log.info("Drill type summary requested");
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_TYPE_SUMMARY)
                .build();
        List<DrillTypePerformanceDTO> response = strategyRegistry.executeList(AnalyticsType.DRILL_TYPE_SUMMARY, request, DrillTypePerformanceDTO.class);
        log.info("Drill type summary completed. count={}", response.size());
        return response;
    }

    @Override
    public DrillTrendsDTO getDrillTrends(int days, String username) {
        log.info("Drill trends requested. days={}, username={}", days, username);
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.DRILL_TRENDS)
                .days(days)
                .username(username)
                .build();
        DrillTrendsDTO response = strategyRegistry.execute(AnalyticsType.DRILL_TRENDS, request, DrillTrendsDTO.class);
        log.info("Drill trends completed. points={}", response == null || response.getPoints() == null ? 0 : response.getPoints().size());
        return response;
    }

    @Override
    public WordDistributionDTO getWordDistribution() {
        log.info("Word distribution requested");
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_DISTRIBUTION)
                .build();
        WordDistributionDTO response = strategyRegistry.execute(AnalyticsType.WORD_DISTRIBUTION, request, WordDistributionDTO.class);
        log.info("Word distribution completed");
        return response;
    }

    @Override
    public UserPerformanceDTO getUserPerformance(String username, int topN) {
        log.info("User performance requested. username={}, topN={}", username, topN);
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.USER_PERFORMANCE)
                .username(username)
                .topN(topN)
                .build();
        UserPerformanceDTO response = strategyRegistry.execute(AnalyticsType.USER_PERFORMANCE, request, UserPerformanceDTO.class);
        log.info("User performance completed. username={}", username);
        return response;
    }

    @Override
    public List<WordDifficultyDTO> getWordDifficultyHeatmap(int topN) {
        log.info("Word difficulty heatmap requested. topN={}", topN);
        AnalyticsRequest request = AnalyticsRequest.builder()
                .type(AnalyticsType.WORD_DIFFICULTY)
                .topN(topN)
                .build();
        List<WordDifficultyDTO> response = strategyRegistry.executeList(AnalyticsType.WORD_DIFFICULTY, request, WordDifficultyDTO.class);
        log.info("Word difficulty heatmap completed. count={}", response.size());
        return response;
    }
}
