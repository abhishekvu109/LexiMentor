package com.abhi.leximentor.leximentor.service.analytics.engine.strategy;

import com.abhi.leximentor.leximentor.dto.analytics.DrillTypeUserPerformanceDTO;
import com.abhi.leximentor.leximentor.dto.analytics.UserPerformanceDTO;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsRequest;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsStrategy;
import com.abhi.leximentor.leximentor.service.analytics.engine.AnalyticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserPerformanceStrategy implements AnalyticsStrategy<UserPerformanceDTO> {
    private static final int DEFAULT_TOP_N = 3;

    private final DrillChallengeRepository drillChallengeRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.USER_PERFORMANCE;
    }

    @Override
    public UserPerformanceDTO execute(AnalyticsRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ServerException().new InternalError("Username is required for user performance analytics.");
        }

        int topN = request.getTopN() == null ? DEFAULT_TOP_N : request.getTopN();

        List<DrillTypeUserPerformanceDTO> rows = new LinkedList<>();
        drillChallengeRepository.findUserDrillTypePerformance(request.getUsername()).forEach(tuple -> {
            String drillType = tuple.get("drillType", String.class);
            long drillCount = tuple.get("drillCount", Long.class);
            double avgScore = tuple.get("avgScore", Double.class);
            long passCount = tuple.get("passCount", Long.class);
            double passRate = drillCount == 0 ? 0.0 : Math.round(((passCount * 100.0) / drillCount) * 100.0) / 100.0;
            rows.add(DrillTypeUserPerformanceDTO.builder()
                    .drillType(drillType)
                    .drillCount(drillCount)
                    .avgScore(Math.round(avgScore * 100.0) / 100.0)
                    .passRate(passRate)
                    .build());
        });

        List<DrillTypeUserPerformanceDTO> topBest = rows.stream()
                .sorted(Comparator.comparingDouble(DrillTypeUserPerformanceDTO::getAvgScore).reversed())
                .limit(topN)
                .toList();

        List<DrillTypeUserPerformanceDTO> topWorst = rows.stream()
                .sorted(Comparator.comparingDouble(DrillTypeUserPerformanceDTO::getAvgScore))
                .limit(topN)
                .toList();

        return UserPerformanceDTO.builder()
                .username(request.getUsername())
                .topBest(topBest)
                .topWorst(topWorst)
                .build();
    }
}
