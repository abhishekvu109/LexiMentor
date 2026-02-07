package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.RoutineStructureDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoutineStructureCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<Integer, Long> drillsPerRoutineDistribution = context.getRoutines().stream()
                .collect(Collectors.groupingBy(routine -> routine.getDrills() == null ? 0 : routine.getDrills().size(), Collectors.counting()));

        double totalDrills = context.getRoutines().stream()
                .mapToDouble(routine -> routine.getDrills() == null ? 0 : routine.getDrills().size())
                .sum();
        double averageDrillsPerRoutine = AnalyticsMath.safeAverage(totalDrills, context.getRoutines().size());

        builder.routineStructure(RoutineStructureDTO.builder()
                .averageDrillsPerRoutine(averageDrillsPerRoutine)
                .drillsPerRoutineDistribution(drillsPerRoutineDistribution)
                .build());
    }
}
