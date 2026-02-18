package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;

import java.util.List;

public interface DrillChallengeService {
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes, String username);

    public List<ChallengeDTO> getChallengesByDrillRefId(long drillRefId);
    public List<ChallengeDTO> getChallengesByDrillRefIdAndUsername(long drillRefId,String username);

    public void deleteChallenge(long drillRefId);

    public List<EvaluatorDTO> getEvaluatorsByChallengeId(long challengeRefId);
}
