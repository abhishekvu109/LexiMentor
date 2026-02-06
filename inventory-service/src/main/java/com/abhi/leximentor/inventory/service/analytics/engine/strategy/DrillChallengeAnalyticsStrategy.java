package com.abhi.leximentor.inventory.service.analytics.engine.strategy;

import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsRequest;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsStrategy;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsType;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeAnalyticsStrategy implements AnalyticsStrategy<List<DrillChallengeAnalyticsDTO>> {
    private final DrillChallengeRepository drillChallengeRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.DRILL_CHALLENGE;
    }

    @Override
    public List<DrillChallengeAnalyticsDTO> execute(AnalyticsRequest request) {
        List<DrillChallengeAnalyticsDTO> drillChallengeAnalyticsDTOS = new LinkedList<>();
        drillChallengeRepository.findDrillAnalyticsGroupedByType().forEach(tuple -> {
            DrillChallengeAnalyticsDTO drillChallengeAnalyticsDTO = DrillChallengeAnalyticsDTO.builder().build();
            String drillType = tuple.get("drillType", String.class);
            drillChallengeAnalyticsDTO.setDrillType(drillType);
            drillChallengeAnalyticsDTO.setDrillCount(tuple.get("drillCount", Long.class));
            drillChallengeAnalyticsDTO.setAvgScore(tuple.get("avgScore", Double.class));
            drillChallengeAnalyticsDTO.setHighestScore(tuple.get("highestScore", Double.class));
            drillChallengeAnalyticsDTO.setLowestScore(tuple.get("lowestScore", Double.class));
            drillChallengeAnalyticsDTO.setTopNBestPerformingDrills(drillChallengeRepository.findTop10ByDrillTypeOrderByDrillScoreDesc(drillType).stream().map(DrillServiceUtil.DrillChallengeUtil::buildDTO).toList());
            drillChallengeAnalyticsDTO.setTopNWorstPerformingDrills(drillChallengeRepository.findTop10ByDrillTypeOrderByDrillScoreAsc(drillType).stream().map(DrillServiceUtil.DrillChallengeUtil::buildDTO).toList());
            drillChallengeAnalyticsDTOS.add(drillChallengeAnalyticsDTO);
        });
        return drillChallengeAnalyticsDTOS;
    }
}
