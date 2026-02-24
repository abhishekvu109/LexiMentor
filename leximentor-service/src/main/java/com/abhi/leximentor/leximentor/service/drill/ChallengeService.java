package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;
import com.abhi.leximentor.leximentor.dto.drill.filters.ChallengeSearchFilter;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;

import java.util.List;
import java.util.Optional;

public interface ChallengeService {
    DrillDTO addChallenges(DrillDTO drillDTO, ChallengeType challengeType, String username);

    List<ChallengeDTO> getChallengesByDrillKey(String drillKey);
    List<ChallengeDTO> getChallengesByDrillRefIdAndUsername(long drillRefId,String username);

    void deleteChallenge(long drillRefId);

    List<EvaluatorDTO> getEvaluatorsByChallengeId(long challengeRefId);

    Optional<List<ChallengeDTO>> search(ChallengeSearchFilter filter);
}
