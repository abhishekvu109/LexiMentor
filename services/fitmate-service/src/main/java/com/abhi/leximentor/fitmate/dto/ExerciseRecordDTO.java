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
public class ExerciseRecordDTO {
    private String exerciseName;
    private double maxMeasurement;
    private LocalDate maxMeasurementDate;
    private int maxRepetitions;
    private LocalDate maxRepetitionsDate;
    private double maxVolume;
    private LocalDate maxVolumeDate;
}
