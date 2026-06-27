package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseProgressionDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExerciseProgressionsCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, List<ExerciseProgressionDTO>> exerciseProgressions = context.getDrills().stream()
                .filter(drill -> drill.getExercise() != null && drill.getExercise().getName() != null)
                .filter(drill -> drill.getCrtnDate() != null)
                .collect(Collectors.groupingBy(drill -> drill.getExercise().getName(),
                        Collectors.groupingBy(drill -> YearMonth.from(drill.getCrtnDate().toLocalDate()),
                                Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .map(monthEntry -> buildProgression(monthEntry.getKey(), monthEntry.getValue()))
                                .sorted(Comparator.comparing(ExerciseProgressionDTO::getDate))
                                .collect(Collectors.toList())
                ));

        builder.exerciseProgressions(exerciseProgressions);
    }

    private ExerciseProgressionDTO buildProgression(YearMonth yearMonth, List<Drill> drillsInMonth) {
        double sumMeasurement = drillsInMonth.stream().mapToDouble(Drill::getMeasurement).sum();
        double averageMeasurement = sumMeasurement / drillsInMonth.size();

        Optional<Drill> maxMeasurementDrill = drillsInMonth.stream()
                .max(Comparator.comparingDouble(Drill::getMeasurement));
        double maxMeasurement = maxMeasurementDrill.map(Drill::getMeasurement).orElse(0.0);

        double sumRepetitions = drillsInMonth.stream().mapToDouble(Drill::getRepetition).sum();
        double averageRepetitions = sumRepetitions / drillsInMonth.size();

        Optional<Drill> maxRepetitionsDrill = drillsInMonth.stream()
                .max(Comparator.comparingInt(Drill::getRepetition));
        int maxRepetitions = maxRepetitionsDrill.map(Drill::getRepetition).orElse(0);

        return ExerciseProgressionDTO.builder()
                .date(yearMonth.atDay(1))
                .averageMeasurement(averageMeasurement)
                .maxMeasurement(maxMeasurement)
                .averageRepetitions(averageRepetitions)
                .maxRepetitions(maxRepetitions)
                .build();
    }
}
