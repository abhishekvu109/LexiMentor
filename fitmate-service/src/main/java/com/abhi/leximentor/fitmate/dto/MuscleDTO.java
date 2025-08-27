package com.abhi.leximentor.fitmate.dto;

import com.abhi.leximentor.fitmate.entities.BodyPart;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class MuscleDTO {
    private String refId;
    private String name;
    private String description;
    private String status;
    private BodyPartsDTO bodyPart;
}
