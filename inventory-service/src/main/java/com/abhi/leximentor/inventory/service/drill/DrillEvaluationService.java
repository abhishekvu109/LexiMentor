package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillReportResponseDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;

import java.util.List;

public interface DrillEvaluationService {
    public DrillEvaluationDTO add(DrillEvaluationDTO dto);

    public List<DrillEvaluationDTO> addAll(List<DrillEvaluationDTO> dtos);

    public List<DrillEvaluationDTO> evaluate(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, String evaluator,long challengeRefId);

    public List<DrillEvaluationDTO> evaluateMeaning(List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, DrillChallenge drillChallenge, String evaluator) throws Exception;

    public void setUrl(String url);

    public DrillReportResponseDTO getEvaluationReport(long challengeRefId);
}
