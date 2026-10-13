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
public class ExerciseDTO {
    private String refId;
    private String name;
    private String description;
    private String unit;
    private String status;
    private String difficultyLevel;
    private String riskLevel;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private TrainingDTO training;
    private BodyPartsDTO bodyPart;
    private List<MuscleDTO> targetMuscles;
    private List<String> equipments;
    private ExerciseAnalyticsDTO analyticsDTOList;
}
