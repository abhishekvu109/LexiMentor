package com.abhi.leximentor.leximentor.service.analytics.engine.strategy;

import com.abhi.leximentor.leximentor.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsRequest;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsStrategy;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeAnalyticsStrategy implements AnalyticsStrategy<List<DrillChallengeAnalyticsDTO>> {
    private final ChallengeRepository challengeRepository;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.DRILL_CHALLENGE;
    }

    @Override
    public List<DrillChallengeAnalyticsDTO> execute(AnalyticsRequest request) {
        List<DrillChallengeAnalyticsDTO> drillChallengeAnalyticsDTOS = new LinkedList<>();
        challengeRepository.findDrillAnalyticsGroupedByType().forEach(tuple -> {
            DrillChallengeAnalyticsDTO drillChallengeAnalyticsDTO = DrillChallengeAnalyticsDTO.builder().build();
            String drillType = tuple.get("drillType", String.class);
            drillChallengeAnalyticsDTO.setDrillType(drillType);
            drillChallengeAnalyticsDTO.setDrillCount(tuple.get("drillCount", Long.class));
            drillChallengeAnalyticsDTO.setAvgScore(tuple.get("avgScore", Double.class));
            drillChallengeAnalyticsDTO.setHighestScore(tuple.get("highestScore", Double.class));
            drillChallengeAnalyticsDTO.setLowestScore(tuple.get("lowestScore", Double.class));
            drillChallengeAnalyticsDTO.setTopNBestPerformingDrills(challengeRepository.findTop10ByChallengeTypeOrderByScoreDesc(drillType).stream().map(drillDomainMapper::toDto).toList());
            drillChallengeAnalyticsDTO.setTopNWorstPerformingDrills(challengeRepository.findTop10ByChallengeTypeOrderByScoreAsc(drillType).stream().map(drillDomainMapper::toDto).toList());
            drillChallengeAnalyticsDTOS.add(drillChallengeAnalyticsDTO);
        });
        return drillChallengeAnalyticsDTOS;
    }
}
