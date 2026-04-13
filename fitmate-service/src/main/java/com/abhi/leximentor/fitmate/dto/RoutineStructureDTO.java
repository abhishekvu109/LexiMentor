package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class RoutineStructureDTO {
    private double averageDrillsPerRoutine;
    private Map<Integer, Long> drillsPerRoutineDistribution;
}
