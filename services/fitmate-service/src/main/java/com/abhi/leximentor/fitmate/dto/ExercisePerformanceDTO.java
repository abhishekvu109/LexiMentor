package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ExercisePerformanceDTO {
    private String exerciseName;
    private long timesCompleted;
    private double totalVolume;
    private double averageVolume;
    private long totalRepetitions;
    private double maxMeasurement;
    private int maxRepetitions;
}
