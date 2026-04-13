package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.dto.inv.EvaluatorDTO;

import java.util.List;

public interface DrillChallengeService {
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes, String username);

    public List<DrillChallengeDTO> getChallengesByDrillRefId(long drillRefId);
    public List<DrillChallengeDTO> getChallengesByDrillRefIdAndUsername(long drillRefId,String username);

    public void deleteChallenge(long drillRefId);

    public List<EvaluatorDTO> getEvaluatorsByChallengeId(long challengeRefId);
}
