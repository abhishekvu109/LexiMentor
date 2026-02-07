package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class TrainingAdherenceDTO {
    private long totalRoutines;
    private long unassignedRoutines;
    private Map<String, Long> routinesPerTraining;
    private Map<String, Double> routineShareByTraining;
    private String topTrainingName;
}
