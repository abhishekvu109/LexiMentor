package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.service.drill.DrillChallengeScoreService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
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
    private final DrillDomainMapper drillDomainMapper;

    @Override
    public ChallengeScoresDTO createChallenge(ChallengeScoresDTO dto) {
        log.info("Creating drill challenge score. challengeRefId={}, drillSetRefId={}", dto == null ? null : dto.getDrillChallengeRefId(), dto == null ? null : dto.getDrillSetRefId());
        Challenge challenge = drillChallengeRepository.findByRefId(Long.parseLong(dto.getDrillChallengeRefId()));
        DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
        DrillTypes drillTypes = DrillTypes.getType(challenge.getChallengeType());
        ChallengeScores scores = drillDomainMapper.toEntity(challenge, drillSet, drillTypes);
        scores = drillChallengeScoreRepository.save(scores);
        ChallengeScoresDTO response = drillDomainMapper.toDto(scores);
        log.info("Created drill challenge score. refId={}", response.getRefId());
        return response;
    }

    @Override
    public List<ChallengeScoresDTO> getByDrillChallengeId(Challenge challenge) {
        log.info("Fetching drill challenge scores. challengeRefId={}", challenge == null ? null : challenge.getRefId());
        List<ChallengeScores> challengeScores = drillChallengeScoreRepository.findByChallengeId(challenge);
        List<ChallengeScoresDTO> response = challengeScores.stream().map(drillDomainMapper::toDto).collect(Collectors.toList());
        log.info("Fetched drill challenge scores. count={}", response.size());
        return response;
    }

    @Override
    public List<ChallengeScoresDTO> updateResponse(List<ChallengeScoresDTO> dtos) {
        log.info("Updating drill challenge scores responses. count={}", dtos == null ? 0 : dtos.size());
        List<ChallengeScoresDTO> output = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            Challenge challenge = null;
            for (ChallengeScoresDTO dto : dtos) {
                challenge = (challenge == null) ? drillChallengeRepository.findByRefId(Long.parseLong(dto.getDrillChallengeRefId())) : challenge;
                DrillSet drillSet = drillSetRepository.findByRefId(Long.parseLong(dto.getDrillSetRefId()));
                ChallengeScores drillChallengeScore = drillChallengeScoreRepository.findByDrillSetIdAndChallengeId(drillSet, challenge);
                drillChallengeScore.setResponse(dto.getResponse());
                drillChallengeScore.setCorrect(dto.isCorrect());
                drillChallengeScore = drillChallengeScoreRepository.save(drillChallengeScore);
                output.add(drillDomainMapper.toDto(drillChallengeScore));
            }
            challenge.setStatus(Status.DrillChallenge.COMPLETED);
            drillChallengeRepository.save(challenge);
        }
        log.info("Updated drill challenge scores responses. count={}", output.size());
        return output;
    }
}
