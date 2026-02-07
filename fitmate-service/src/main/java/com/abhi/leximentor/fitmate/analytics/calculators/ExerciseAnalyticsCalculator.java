package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.service.impl.FitmateServiceUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExerciseAnalyticsCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, ExerciseAnalyticsDTO> exerciseAnalytics = context.getDrillsByExerciseName().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildAnalytics(entry.getValue())
                ));

        builder.exerciseAnalytics(exerciseAnalytics);
    }

    private ExerciseAnalyticsDTO buildAnalytics(List<Drill> drillsForExercise) {
        long totalNumberOfTimesCompleted = drillsForExercise.size();

        double monthlyAverage = drillsForExercise.stream()
                .filter(drill -> drill.getCrtnDate() != null)
                .filter(drill -> drill.getCrtnDate().getMonth() == LocalDateTime.now().getMonth()
                        && drill.getCrtnDate().getYear() == LocalDateTime.now().getYear())
                .count();

        List<DrillDTO> lastFiveDrills = drillsForExercise.stream()
                .sorted(Comparator.comparing(Drill::getCrtnDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(FitmateServiceUtil.DrillUtil::buildDTO)
                .collect(Collectors.toList());

        Optional<Drill> maxMeasurementDrill = drillsForExercise.stream()
                .max(Comparator.comparingDouble(Drill::getMeasurement));
        double maxMeasurement = maxMeasurementDrill.map(Drill::getMeasurement).orElse(0.0);

        Optional<Drill> maxRepetitionsDrill = drillsForExercise.stream()
                .max(Comparator.comparingInt(Drill::getRepetition));
        int maxRepetitions = maxRepetitionsDrill.map(Drill::getRepetition).orElse(0);

        return ExerciseAnalyticsDTO.builder()
                .totalNumberOfTimesCompleted((int) totalNumberOfTimesCompleted)
                .monthlyAverage(monthlyAverage)
                .lastFiveDrills(lastFiveDrills)
                .maxMeasurement(maxMeasurement)
                .maxRepetitions(maxRepetitions)
                .build();
    }
}
