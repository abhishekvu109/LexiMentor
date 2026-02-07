package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExercisePerformanceDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExercisePerformanceCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<ExercisePerformanceDTO> performance = context.getDrillsByExerciseName().entrySet().stream()
                .map(entry -> buildPerformance(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ExercisePerformanceDTO::getTotalVolume).reversed())
                .collect(Collectors.toList());

        builder.exercisePerformance(performance);
    }

    private ExercisePerformanceDTO buildPerformance(String exerciseName, List<Drill> drills) {
        long timesCompleted = drills.size();
        double totalVolume = drills.stream().mapToDouble(AnalyticsMath::volume).sum();
        long totalReps = drills.stream().mapToLong(Drill::getRepetition).sum();
        double averageVolume = AnalyticsMath.safeAverage(totalVolume, timesCompleted);

        double maxMeasurement = drills.stream().mapToDouble(Drill::getMeasurement).max().orElse(0.0);
        int maxReps = drills.stream().mapToInt(Drill::getRepetition).max().orElse(0);

        return ExercisePerformanceDTO.builder()
                .exerciseName(exerciseName)
                .timesCompleted(timesCompleted)
                .totalVolume(totalVolume)
                .averageVolume(averageVolume)
                .totalRepetitions(totalReps)
                .maxMeasurement(maxMeasurement)
                .maxRepetitions(maxReps)
                .build();
    }
}
