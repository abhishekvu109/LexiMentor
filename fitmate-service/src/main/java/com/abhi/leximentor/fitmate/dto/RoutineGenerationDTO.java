package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class RoutineGenerationDTO {
    private String trainingType;
    private List<String> targetBodyParts;
    private String username;
}
