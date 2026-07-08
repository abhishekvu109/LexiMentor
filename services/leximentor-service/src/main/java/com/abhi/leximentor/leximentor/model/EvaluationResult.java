package com.abhi.leximentor.leximentor.model;

import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EvaluationResult {
    private boolean isSuccess;
    private List<ChallengeEvaluationDTO> result;
}
