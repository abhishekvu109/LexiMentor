package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoutineDistributionByTrainingCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, Long> routineDistributionByTrainingType = context.getRoutines().stream()
                .filter(routine -> routine.getTraining() != null && routine.getTraining().getName() != null)
                .map(routine -> routine.getTraining().getName())
                .collect(Collectors.groupingBy(trainingName -> trainingName, Collectors.counting()));

        builder.routineDistributionByTrainingType(routineDistributionByTrainingType);
    }
}
