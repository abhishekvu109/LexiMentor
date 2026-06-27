package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeScoreRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.service.drill.ChallengeScoreService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChallengeScoreServiceImpl implements ChallengeScoreService {
    private final ChallengeScoreRepository challengeScoreRepository;
    private final ChallengeRepository challengeRepository;
    private final DrillSetRepository drillSetRepository;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    public ChallengeScoresDTO createChallenge(ChallengeScoresDTO dto) {
        log.info("Creating drill challenge score. challengeKey={}, drillSetKey={}", dto == null ? null : dto.getChallengeKey(), dto == null ? null : dto.getDrillSetKey());
        Challenge challenge = challengeRepository.findByKey(dto.getChallengeKey());
        DrillSet drillSet = drillSetRepository.findByKey(dto.getDrillSetKey())
                .orElseThrow(() -> new EntityNotFoundException("Drill set not found for key: " + dto.getDrillSetKey()));
        ChallengeType challengeType = challenge.getChallengeType();
        ChallengeScores scores = drillDomainMapper.toEntity(challenge, drillSet, challengeType);
        scores = challengeScoreRepository.save(scores);
        ChallengeScoresDTO response = drillDomainMapper.toDto(scores);
        log.info("Created drill challenge score. key={}", response.getKey());
        return response;
    }

    @Override
    public List<ChallengeScoresDTO> getByDrillChallengeId(Challenge challenge) {
        log.info("Fetching drill challenge scores. challengeKey={}", challenge == null ? null : challenge.getKey());
        List<ChallengeScores> challengeScores = challengeScoreRepository.findByChallenge(challenge);
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
                challenge = (challenge == null) ? challengeRepository.findByKey(dto.getChallengeKey()) : challenge;
                DrillSet drillSet = drillSetRepository.findByKey(dto.getDrillSetKey())
                        .orElseThrow(() -> new EntityNotFoundException("Drill set not found for key: " + dto.getDrillSetKey()));
                ChallengeScores drillChallengeScore = challengeScoreRepository.findByDrillSetAndChallenge(drillSet, challenge);
                drillChallengeScore.setResponse(dto.getResponse());
                drillChallengeScore.setCorrect(dto.isCorrect());
                drillChallengeScore = challengeScoreRepository.save(drillChallengeScore);
                output.add(drillDomainMapper.toDto(drillChallengeScore));
            }
            challenge.setStatus(Status.DrillChallenge.COMPLETED);
            challengeRepository.save(challenge);
        }
        log.info("Updated drill challenge scores responses. count={}", output.size());
        return output;
    }
}
