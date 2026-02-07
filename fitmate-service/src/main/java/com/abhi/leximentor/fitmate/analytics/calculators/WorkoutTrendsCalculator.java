package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.WorkoutTrendDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WorkoutTrendsCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<LocalDate, List<Routine>> routinesByDate = context.getRoutinesByDate();

        List<WorkoutTrendDTO> workoutTrends = routinesByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Routine> routinesOnDate = entry.getValue();
                    long routinesCompleted = routinesOnDate.size();
                    double workoutDurationMinutes = routinesOnDate.stream()
                            .filter(routine -> routine.getDurationInMinutes() > 0)
                            .mapToDouble(Routine::getDurationInMinutes)
                            .sum();
                    double caloriesBurnt = routinesOnDate.stream()
                            .mapToDouble(Routine::getBurntCalories)
                            .sum();
                    return WorkoutTrendDTO.builder()
                            .date(date)
                            .routinesCompleted(routinesCompleted)
                            .workoutDurationMinutes(workoutDurationMinutes)
                            .caloriesBurnt(caloriesBurnt)
                            .build();
                })
                .sorted(Comparator.comparing(WorkoutTrendDTO::getDate))
                .collect(Collectors.toList());

        builder.workoutTrends(workoutTrends);
    }
}
