package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseFrequencyDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MostFrequentExercisesCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<ExerciseFrequencyDTO> mostFrequentExercises = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getName() != null)
                .map(Exercise::getName)
                .collect(Collectors.groupingBy(exerciseName -> exerciseName, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> ExerciseFrequencyDTO.builder()
                        .exerciseName(entry.getKey())
                        .frequency(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ExerciseFrequencyDTO::getFrequency).reversed())
                .limit(10)
                .collect(Collectors.toList());

        builder.mostFrequentExercises(mostFrequentExercises);
    }
}
