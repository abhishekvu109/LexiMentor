package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.TrainingAdherenceDTO;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TrainingAdherenceCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        long totalRoutines = context.getRoutines().size();
        long unassigned = context.getRoutines().stream().filter(routine -> routine.getTraining() == null).count();

        Map<String, Long> routinesPerTraining = context.getRoutines().stream()
                .filter(routine -> routine.getTraining() != null && routine.getTraining().getName() != null)
                .collect(Collectors.groupingBy(routine -> routine.getTraining().getName(), Collectors.counting()));

        Map<String, Double> routineShareByTraining = routinesPerTraining.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> totalRoutines > 0 ? entry.getValue() / (double) totalRoutines : 0.0));

        String topTraining = routinesPerTraining.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        builder.trainingAdherence(TrainingAdherenceDTO.builder()
                .totalRoutines(totalRoutines)
                .unassignedRoutines(unassigned)
                .routinesPerTraining(routinesPerTraining)
                .routineShareByTraining(routineShareByTraining)
                .topTrainingName(topTraining)
                .build());
    }
}
