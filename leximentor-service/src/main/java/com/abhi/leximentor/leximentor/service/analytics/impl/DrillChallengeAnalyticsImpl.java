package com.abhi.leximentor.leximentor.service.analytics.impl;

import com.abhi.leximentor.leximentor.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.service.analytics.DrillChallengeAnalyticsService;
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
    private final ChallengeRepository challengeRepository;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    public List<DrillChallengeAnalyticsDTO> getDrillChallengeMetadataAnalytics() {
        log.info("Building drill challenge metadata analytics");
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
        log.info("Built drill challenge metadata analytics. count={}", drillChallengeAnalyticsDTOS.size());
        return drillChallengeAnalyticsDTOS;
    }
}
