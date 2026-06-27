package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ExerciseAnalyticsDTO {
    private int totalNumberOfTimesCompleted;
    private double monthlyAverage;
    private List<DrillDTO> lastFiveDrills;
    private double maxMeasurement; // New field
    private int maxRepetitions;    // New field
}
