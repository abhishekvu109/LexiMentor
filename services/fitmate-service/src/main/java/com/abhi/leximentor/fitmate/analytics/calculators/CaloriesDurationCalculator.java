package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.CaloriesDurationDTO;
import com.abhi.leximentor.fitmate.dto.RoutinePerformanceDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class CaloriesDurationCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        List<Routine> routines = context.getRoutines();

        double totalCalories = routines.stream().mapToDouble(Routine::getBurntCalories).sum();
        double totalDurationMinutes = routines.stream().mapToDouble(Routine::getDurationInMinutes).sum();
        double averageCaloriesPerRoutine = AnalyticsMath.safeAverage(totalCalories, routines.size());
        double averageDurationMinutes = AnalyticsMath.safeAverage(totalDurationMinutes, routines.size());

        Optional<Routine> bestCalories = routines.stream().max(Comparator.comparingDouble(Routine::getBurntCalories));
        Optional<Routine> worstCalories = routines.stream().min(Comparator.comparingDouble(Routine::getBurntCalories));
        Optional<Routine> bestDuration = routines.stream().max(Comparator.comparingDouble(Routine::getDurationInMinutes));
        Optional<Routine> worstDuration = routines.stream().min(Comparator.comparingDouble(Routine::getDurationInMinutes));

        builder.caloriesDuration(CaloriesDurationDTO.builder()
                .totalCalories(totalCalories)
                .averageCaloriesPerRoutine(averageCaloriesPerRoutine)
                .totalDurationMinutes(totalDurationMinutes)
                .averageDurationMinutes(averageDurationMinutes)
                .bestByCalories(bestCalories.map(this::toPerformance).orElse(null))
                .worstByCalories(worstCalories.map(this::toPerformance).orElse(null))
                .bestByDuration(bestDuration.map(this::toPerformance).orElse(null))
                .worstByDuration(worstDuration.map(this::toPerformance).orElse(null))
                .build());
    }

    private RoutinePerformanceDTO toPerformance(Routine routine) {
        if (routine == null) {
            return null;
        }
        return RoutinePerformanceDTO.builder()
                .routineRefId(routine.getRefId())
                .routineDate(routine.getRoutineDate())
                .caloriesBurnt(routine.getBurntCalories())
                .durationMinutes(routine.getDurationInMinutes())
                .trainingName(routine.getTraining() == null ? null : routine.getTraining().getName())
                .drillsCount(routine.getDrills() == null ? 0 : routine.getDrills().size())
                .build();
    }
}
