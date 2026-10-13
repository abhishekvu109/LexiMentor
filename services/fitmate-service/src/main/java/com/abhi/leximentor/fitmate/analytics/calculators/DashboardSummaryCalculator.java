package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.DashboardSummaryDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

@Component
public class DashboardSummaryCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        long totalRoutinesCompleted = context.getRoutines().size();
        double totalWorkoutDurationMinutes = context.getRoutines().stream()
                .filter(routine -> routine.getDurationInMinutes() > 0)
                .mapToDouble(Routine::getDurationInMinutes)
                .sum();
        double totalCaloriesBurnt = context.getRoutines().stream()
                .mapToDouble(Routine::getBurntCalories)
                .sum();

        long totalUniqueExercises = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getName() != null)
                .map(Exercise::getName)
                .distinct()
                .count();

        builder.summary(DashboardSummaryDTO.builder()
                .totalRoutinesCompleted(totalRoutinesCompleted)
                .totalWorkoutDurationMinutes(totalWorkoutDurationMinutes)
                .totalCaloriesBurnt(totalCaloriesBurnt)
                .totalUniqueExercises(totalUniqueExercises)
                .build());
    }
}
