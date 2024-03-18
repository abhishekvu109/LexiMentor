package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class EvaluatorDTO {
    private long refId;
    private String name;
    private LocalDateTime crtnDate;
    private String status;
    private String drillType;
}
