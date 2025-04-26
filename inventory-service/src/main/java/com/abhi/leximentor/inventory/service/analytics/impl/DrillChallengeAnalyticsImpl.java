package com.abhi.leximentor.inventory.service.analytics.impl;

import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.analytics.DrillChallengeAnalyticsService;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeAnalyticsImpl implements DrillChallengeAnalyticsService {
    private final DrillChallengeRepository drillChallengeRepository;

    @Override
    public List<DrillChallengeAnalyticsDTO> getDrillChallengeMetadataAnalytics() {
        List<DrillChallengeAnalyticsDTO> drillChallengeAnalyticsDTOS = new LinkedList<>();
        drillChallengeRepository.findDrillAnalyticsGroupedByType().forEach(tuple -> {
            DrillChallengeAnalyticsDTO drillChallengeAnalyticsDTO = DrillChallengeAnalyticsDTO.builder().build();
            String drillType = tuple.get("drillType", String.class);
            drillChallengeAnalyticsDTO.setDrillType(drillType);
            drillChallengeAnalyticsDTO.setDrillCount(tuple.get("drillCount", Integer.class));
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
