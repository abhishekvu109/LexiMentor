package com.abhi.leximentor.leximentor.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class EvaluatorDTO {
    private String key;
    private String name;
    private LocalDateTime createdAt;
    private String status;
    private String challengeType;
}
