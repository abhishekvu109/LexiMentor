package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.DifficultyRiskMixDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DifficultyRiskMixCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<Integer, Long> difficultyCounts = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null)
                .map(Exercise::getDifficultyLevel)
                .collect(Collectors.groupingBy(level -> level, Collectors.counting()));

        Map<Integer, Long> riskCounts = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null)
                .map(Exercise::getRiskLevel)
                .collect(Collectors.groupingBy(level -> level, Collectors.counting()));

        long difficultyTotal = difficultyCounts.values().stream().mapToLong(Long::longValue).sum();
        long riskTotal = riskCounts.values().stream().mapToLong(Long::longValue).sum();

        double difficultySum = difficultyCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum();

        double riskSum = riskCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum();

        builder.difficultyRiskMix(DifficultyRiskMixDTO.builder()
                .averageDifficultyLevel(AnalyticsMath.safeAverage(difficultySum, difficultyTotal))
                .averageRiskLevel(AnalyticsMath.safeAverage(riskSum, riskTotal))
                .difficultyCounts(difficultyCounts)
                .riskCounts(riskCounts)
                .build());
    }
}
