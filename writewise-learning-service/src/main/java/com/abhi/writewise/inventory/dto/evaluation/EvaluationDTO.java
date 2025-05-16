package com.abhi.writewise.inventory.dto.evaluation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationDTO {
    private String refId;
    private String uuid;
    private EvaluationResultDTO evaluationResult;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private String evaluationStatus;
    private double score;
}
