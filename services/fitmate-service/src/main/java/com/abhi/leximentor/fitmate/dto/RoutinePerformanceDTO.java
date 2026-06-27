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
public class RoutinePerformanceDTO {
    private long routineRefId;
    private LocalDate routineDate;
    private double caloriesBurnt;
    private double durationMinutes;
    private String trainingName;
    private int drillsCount;
}
