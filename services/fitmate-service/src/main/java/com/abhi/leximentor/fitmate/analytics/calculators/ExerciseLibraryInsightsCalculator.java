package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseLibraryInsightsDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExerciseLibraryInsightsCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<Exercise> allExercises = context.getAllExercises();

        Set<String> usedExerciseNames = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getName() != null)
                .map(Exercise::getName)
                .collect(Collectors.toSet());

        List<String> unusedExerciseNames = allExercises.stream()
                .filter(exercise -> exercise.getName() != null)
                .map(Exercise::getName)
                .filter(name -> !usedExerciseNames.contains(name))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        long exercisesWithResources = allExercises.stream()
                .filter(exercise -> exercise.getResources() != null && !exercise.getResources().isEmpty())
                .count();

        long totalExercises = allExercises.size();
        long usedExercises = usedExerciseNames.size();
        long unusedExercises = unusedExerciseNames.size();
        long exercisesWithoutResources = totalExercises - exercisesWithResources;

        builder.exerciseLibraryInsights(ExerciseLibraryInsightsDTO.builder()
                .totalExercises(totalExercises)
                .usedExercises(usedExercises)
                .unusedExercises(unusedExercises)
                .unusedExerciseNames(unusedExerciseNames)
                .exercisesWithResources(exercisesWithResources)
                .exercisesWithoutResources(exercisesWithoutResources)
                .build());
    }
}
