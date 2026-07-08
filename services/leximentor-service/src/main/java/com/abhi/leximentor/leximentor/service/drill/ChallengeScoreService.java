package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;

import java.util.List;

public interface ChallengeScoreService {
    public ChallengeScoresDTO createChallenge(ChallengeScoresDTO dto);

    public List<ChallengeScoresDTO> updateResponse(List<ChallengeScoresDTO> dtos);

    public List<ChallengeScoresDTO> getByDrillChallengeId(Challenge challenge);
}
