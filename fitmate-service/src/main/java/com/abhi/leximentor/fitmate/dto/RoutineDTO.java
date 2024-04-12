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
public class RoutineDTO {
    private String refId;
    private String description;
    private boolean isCompleted;
    private String completionUnit;
    private int measurement;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private ExerciseDTO excerciseDTO;
}
