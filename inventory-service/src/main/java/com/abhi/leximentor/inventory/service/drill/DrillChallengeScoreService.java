package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;

import java.util.List;

public interface DrillChallengeScoreService {
    public DrillChallengeScoresDTO createChallenge(DrillChallengeScoresDTO dto);

    public List<DrillChallengeScoresDTO> getByDrillChallengeId(DrillChallenge drillChallenge);
}
