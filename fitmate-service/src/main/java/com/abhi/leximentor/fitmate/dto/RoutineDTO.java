package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class RoutineDTO {
    private String refId;
    private String key;
    private String description;
    private String status;
    private String workoutDate;
    private LocalDateTime crtnDate;
    private TrainingDTO training;
    private List<DrillDTO> drills;
    private double burntCalories;
    private double durationInMinutes;
    private String username;
}
