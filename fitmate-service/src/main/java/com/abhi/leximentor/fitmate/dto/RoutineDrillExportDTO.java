package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RoutineDrillExportDTO {
    private String username;
    private String routineRefId;
    private LocalDate workoutDate;
    private String routineStatus;
    private String trainingName;
    private double routineBurntCalories;
    private double routineDurationInMinutes;

    private String drillRefId;
    private String exerciseName;
    private String exerciseUnit;
    private String exerciseDifficultyLevel;
    private String exerciseRiskLevel;
    private String targetBodyPart;
    private String targetMuscles;
    private String drillMeasurementUnit;
    private double drillMeasurement;
    private String drillUnit;
    private int drillRepetition;
    private double drillBurntCalories;
    private String drillNotes;
}
