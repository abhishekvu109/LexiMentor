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
public class TrainingMetadataDTO {
    private String refId;
    private String name;
    private String description;
    private String status;
    private LocalDateTime crtnDate;
    private LocalDateTime lastUpdDate;
}
