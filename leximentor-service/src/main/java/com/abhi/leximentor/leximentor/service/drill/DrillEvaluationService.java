package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeEvaluationDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillReportResponseDTO;

import java.util.List;

public interface DrillEvaluationService {
    public ChallengeEvaluationDTO add(ChallengeEvaluationDTO dto);

    public List<ChallengeEvaluationDTO> addAll(List<ChallengeEvaluationDTO> dtos);

    public List<ChallengeEvaluationDTO> evaluate(List<ChallengeScoresDTO> ChallengeScoresDTOS, String evaluator,long challengeRefId);

    public DrillReportResponseDTO getEvaluationReport(long challengeRefId);
}
