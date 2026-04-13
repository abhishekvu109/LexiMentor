package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class RoutineEfficiencyDTO {
    private double averageCaloriesPerMinute;
    private double averageDrillsPerMinute;
    // Potentially add more granular data like per training type or per body part
}
