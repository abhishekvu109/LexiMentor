package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillReportResponseDTO;

import java.util.List;

public interface DrillEvaluationService {
    public DrillEvaluationDTO add(DrillEvaluationDTO dto);

    public List<DrillEvaluationDTO> addAll(List<DrillEvaluationDTO> dtos);

    public List<DrillEvaluationDTO> evaluateMeaning(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator);

    public void setUrl(String url);

    public DrillReportResponseDTO getEvaluationReport(long challengeRefId);
}
