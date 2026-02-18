package com.abhi.leximentor.leximentor.service.analytics.engine.strategy;

import com.abhi.leximentor.leximentor.dto.analytics.DrillTypePerformanceDTO;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
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
public class DrillTypeSummaryStrategy implements AnalyticsStrategy<List<DrillTypePerformanceDTO>> {
    private final DrillChallengeRepository drillChallengeRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.DRILL_TYPE_SUMMARY;
    }

    @Override
    public List<DrillTypePerformanceDTO> execute(AnalyticsRequest request) {
        List<DrillTypePerformanceDTO> results = new LinkedList<>();
        drillChallengeRepository.findDrillTypePerformance().forEach(tuple -> {
            String drillType = tuple.get("drillType", String.class);
            long drillCount = tuple.get("drillCount", Long.class);
            double avgScore = tuple.get("avgScore", Double.class);
            long passCount = tuple.get("passCount", Long.class);
            double passRate = drillCount == 0 ? 0.0 : Math.round(((passCount * 100.0) / drillCount) * 100.0) / 100.0;
            results.add(DrillTypePerformanceDTO.builder()
                    .drillType(drillType)
                    .drillCount(drillCount)
                    .avgScore(avgScore)
                    .passRate(passRate)
                    .build());
        });
        return results;
    }
}
