package com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation {
    private long refId;
    private String uuid;
    private EvaluationResult evaluationResult;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private int evaluationStatus;
    private double score;
}
