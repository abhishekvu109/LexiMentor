package com.abhi.leximentor.fitmate.analytics.calculators;

import com.abhi.leximentor.fitmate.analytics.AnalyticsCalculator;
import com.abhi.leximentor.fitmate.analytics.AnalyticsContext;
import com.abhi.leximentor.fitmate.dto.AnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.EquipmentUsageDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EquipmentUsageCalculator implements AnalyticsCalculator {
    @Override
    public void apply(AnalyticsContext context, AnalyticsDTO.AnalyticsDTOBuilder builder) {
        Map<String, Long> equipmentUsageCounts = context.getDrills().stream()
                .map(Drill::getExercise)
                .filter(exercise -> exercise != null && exercise.getEquipments() != null)
                .flatMap(exercise -> exercise.getEquipments().stream())
                .filter(equipment -> equipment != null && !equipment.isBlank())
                .collect(Collectors.groupingBy(equipment -> equipment, Collectors.counting()));

        long uniqueEquipmentCount = equipmentUsageCounts.size();

        builder.equipmentUsage(EquipmentUsageDTO.builder()
                .uniqueEquipmentCount(uniqueEquipmentCount)
                .equipmentUsageCounts(equipmentUsageCounts)
                .build());
    }
}
