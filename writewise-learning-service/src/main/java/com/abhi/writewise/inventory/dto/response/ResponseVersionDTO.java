package com.abhi.writewise.inventory.dto.response;

import com.abhi.writewise.inventory.dto.evaluation.EvaluationDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVersionDTO {
    private String refId;
    private String uuid;
    private int versionNumber;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private String status;
    private boolean isLatest;
    private String response;
    private String responseStatus;
    private EvaluationDTO evaluation;
    private List<EvaluationDTO> evaluations;
    private String llmEvaluationText;
    private String llmEvaluationStatus;
    private String lowLevelEvaluationText;
    private String lowLevelEvaluationStatus;
}
