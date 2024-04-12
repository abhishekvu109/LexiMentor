package com.abhi.leximentor.fitmate.dto;

import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
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
public class ExcerciseDTO {
    private String refId;
    private String name;
    private String description;
    private String unit;
    private String status;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
    private TrainingMetadataDTO trainingMetadata;
    private BodyPartsDTO targetBodyPart;
    private List<String> secondaryBodyParts;
}
