package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeScoreService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
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
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(dto.getDrillChallengeRefId()));
        DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
        DrillChallengeScores scores = DrillServiceUtil.DrillChallengeScoreUtil.buildEntity(drillChallenge, drillSet);
        scores = drillChallengeScoreRepository.save(scores);
        return DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(scores);
    }

    @Override
    public List<DrillChallengeScoresDTO> getByDrillChallengeId(DrillChallenge drillChallenge) {
        List<DrillChallengeScores> drillChallengeScores = drillChallengeScoreRepository.findByChallengeId(drillChallenge);
        return drillChallengeScores.stream().map(DrillServiceUtil.DrillChallengeScoreUtil::buildDTO).collect(Collectors.toList());
    }

    @Override
    public List<DrillChallengeScoresDTO> updateResponse(List<DrillChallengeScoresDTO> dtos) {
        List<DrillChallengeScoresDTO> output = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            DrillChallenge drillChallenge = null;
            for (DrillChallengeScoresDTO dto : dtos) {
                drillChallenge = (drillChallenge == null) ? drillChallengeRepository.findByRefId(Long.parseLong(dto.getDrillChallengeRefId())) : drillChallenge;
                DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                DrillChallengeScores drillChallengeScore = drillChallengeScoreRepository.findByDrillSetIdAndChallengeId(drillSet, drillChallenge);
                drillChallengeScore.setResponse(dto.getResponse());
                drillChallengeScore = drillChallengeScoreRepository.save(drillChallengeScore);
                output.add(DrillServiceUtil.DrillChallengeScoreUtil.buildDTO(drillChallengeScore));
            }
            drillChallenge.setStatus(Status.DrillChallenge.COMPLETED);
            drillChallengeRepository.save(drillChallenge);
        }
        return output;
    }
}
