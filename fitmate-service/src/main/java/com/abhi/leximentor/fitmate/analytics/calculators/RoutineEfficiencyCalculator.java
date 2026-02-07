package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.RoutineEfficiencyDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

@Component
public class RoutineEfficiencyCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        double totalCalories = 0.0;
        double totalDuration = 0.0;
        long totalDrills = 0;

        for (Routine routine : context.getRoutines()) {
            if (routine.getDurationInMinutes() > 0) {
                totalCalories += routine.getBurntCalories();
                totalDuration += routine.getDurationInMinutes();
                if (routine.getDrills() != null) {
                    totalDrills += routine.getDrills().size();
                }
            }
        }

        double averageCaloriesPerMinute = totalDuration > 0 ? totalCalories / totalDuration : 0.0;
        double averageDrillsPerMinute = totalDuration > 0 ? (double) totalDrills / totalDuration : 0.0;

        builder.routineEfficiency(RoutineEfficiencyDTO.builder()
                .averageCaloriesPerMinute(averageCaloriesPerMinute)
                .averageDrillsPerMinute(averageDrillsPerMinute)
                .build());
    }
}
