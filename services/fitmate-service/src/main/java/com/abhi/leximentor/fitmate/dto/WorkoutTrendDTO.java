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
public class WorkoutTrendDTO {
    private LocalDate date;
    private long routinesCompleted;
    private double workoutDurationMinutes;
    private double caloriesBurnt;
}
