package com.abhi.leximentor.inventory.service.analytics.engine.strategy;

import com.abhi.leximentor.inventory.dto.analytics.DrillTrendPointDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTrendsDTO;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsRequest;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsStrategy;
import com.abhi.leximentor.inventory.service.analytics.engine.AnalyticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillTrendStrategy implements AnalyticsStrategy<DrillTrendsDTO> {
    private static final int DEFAULT_DAYS = 30;

    private final DrillChallengeRepository drillChallengeRepository;

    @Override
    public AnalyticsType getType() {
        return AnalyticsType.DRILL_TRENDS;
    }

    @Override
    public DrillTrendsDTO execute(AnalyticsRequest request) {
        int days = request.getDays() == null ? DEFAULT_DAYS : request.getDays();
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(Math.max(days - 1, 0));

        List<Object[]> rows = drillChallengeRepository.findDrillTrends(fromDate, request.getUsername());
        List<DrillTrendPointDTO> points = new LinkedList<>();
        for (Object[] row : rows) {
            LocalDate date = row[0] instanceof Date ? ((Date) row[0]).toLocalDate() : LocalDate.parse(String.valueOf(row[0]));
            double avgScore = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
            long drillCount = row[2] == null ? 0L : ((Number) row[2]).longValue();
            points.add(DrillTrendPointDTO.builder()
                    .date(date)
                    .avgScore(Math.round(avgScore * 100.0) / 100.0)
                    .drillCount(drillCount)
                    .build());
        }

        return DrillTrendsDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .username(request.getUsername())
                .points(points)
                .build();
    }
}
