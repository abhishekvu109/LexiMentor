package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class DrillDTO {
    private String refId;
    private ExerciseDTO exercise;
    private String routine;
    private String measurementUnit;
    private double measurement;
    private String unit;
    private int repetition;
    private double burntCalories;
    private String notes;
    private LocalDateTime creationDate;
    private MuscleDTO muscle;
}
