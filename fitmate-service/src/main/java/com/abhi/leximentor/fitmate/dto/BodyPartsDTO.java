package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class BodyPartsDTO {
    private String refId;
    private String name;
    private String primaryName;
    private String description;
    private String status;
}
