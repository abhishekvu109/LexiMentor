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
    List<ChallengeDTO> getChallengesByDrillKeyAndUsername(String drillKey,String username);

    void deleteChallenge(String drillKey);

    List<EvaluatorDTO> getEvaluatorsByChallengeKey(String challengeKey);

    Optional<List<ChallengeDTO>> search(ChallengeSearchFilter filter);
}
