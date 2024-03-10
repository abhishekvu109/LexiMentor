package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeScoreServiceImpl implements DrillChallengeScoreService {
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;
    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillSetRepository drillSetRepository;

    @Override
    public DrillChallengeScoresDTO createChallenge(DrillChallengeScoresDTO dto) {
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(dto.getDrillChallengeRefId());
        DrillSet drillSet = drillSetRepository.findByRefId(dto.getDrillSetRefId());
        DrillChallengeScores scores = DrillServiceUtil.DrillChallengeScoreUtil.buildEntity(drillChallenge, drillSet);
        scores = drillChallengeScoreRepository.save(scores);
        return DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(scores);
    }

    @Override
    public List<DrillChallengeScoresDTO> getByDrillChallengeId(DrillChallenge drillChallenge) {
        List<DrillChallengeScores> drillChallengeScores = drillChallengeScoreRepository.findByChallengeId(drillChallenge);
        return drillChallengeScores.stream().map(DrillServiceUtil.DrillChallengeScoreUtil::buildDTO).collect(Collectors.toList());
    }
}
