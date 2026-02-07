package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BodyPartWorkoutVolumeCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, Long> bodyPartWorkoutVolume = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getTargetBodyPart() != null)
                .map(Exercise::getTargetBodyPart)
                .filter(bodyPart -> bodyPart.getName() != null)
                .collect(Collectors.groupingBy(bodyPart -> bodyPart.getName(), Collectors.counting()));

        builder.bodyPartWorkoutVolume(bodyPartWorkoutVolume);
    }
}
