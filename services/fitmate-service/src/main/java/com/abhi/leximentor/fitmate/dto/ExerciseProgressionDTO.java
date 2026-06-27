package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class ExerciseProgressionDTO {
    private LocalDate date; // Date for which this data point is valid (e.g., month start, week start)
    private double averageMeasurement;
    private double maxMeasurement;
    private double averageRepetitions;
    private int maxRepetitions;
}
