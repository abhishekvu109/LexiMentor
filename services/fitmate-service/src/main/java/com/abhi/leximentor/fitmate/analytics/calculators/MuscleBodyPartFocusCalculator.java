package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.analytics.AnalyticsMath;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.MuscleBodyPartFocusDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Muscle;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MuscleBodyPartFocusCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, Long> bodyPartCounts = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getTargetBodyPart() != null)
                .map(Exercise::getTargetBodyPart)
                .filter(bodyPart -> bodyPart.getName() != null)
                .collect(Collectors.groupingBy(bodyPart -> bodyPart.getName(), Collectors.counting()));

        Map<String, Double> bodyPartVolume = context.getDrills().stream()
                .filter(drill -> drill.getExercise() != null && drill.getExercise().getTargetBodyPart() != null)
                .collect(Collectors.groupingBy(
                        drill -> drill.getExercise().getTargetBodyPart().getName(),
                        Collectors.summingDouble(AnalyticsMath::volume)
                ));

        Map<String, Long> muscleCounts = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getTargetMuscles() != null)
                .flatMap(exercise -> exercise.getTargetMuscles().stream())
                .filter(muscle -> muscle.getName() != null)
                .collect(Collectors.groupingBy(Muscle::getName, Collectors.counting()));

        builder.muscleBodyPartFocus(MuscleBodyPartFocusDTO.builder()
                .bodyPartCounts(bodyPartCounts)
                .bodyPartVolume(bodyPartVolume)
                .muscleCounts(muscleCounts)
                .build());
    }
}
