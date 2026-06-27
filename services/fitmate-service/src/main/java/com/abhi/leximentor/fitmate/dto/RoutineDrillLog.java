package com.abhi.leximentor.fitmate.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class RoutineDrillLog {
    private String training;
    private LocalDate routineDate;
    private String exerciseName;
    private String unit;
    private double measurement;
    private String measurementUnit;
    private int repetition;
    private String bodyPartName;
}
