package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.DrillTypes;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.leximentor.service.base.AbstractApplicationService;
import com.abhi.leximentor.leximentor.service.drill.DrillChallengeService;
import com.abhi.leximentor.leximentor.service.inv.EvaluatorService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeServiceImpl extends AbstractApplicationService implements DrillChallengeService {

    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillMetadataRepository drillMetadataRepository;
    private final DrillEvaluationRepository drillEvaluationRepository;
    private final EvaluatorService evaluatorService;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    @Transactional
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes, String username) {
        log.info("Adding challenge. drillRefId={}, drillType={}", drillMetadataDTO == null ? null : drillMetadataDTO.getRefId(), drillTypes);
        DrillMetadata drillMetadata = requireEntity(drillMetadataRepository.findByRefId(parseRefId(drillMetadataDTO.getRefId(), "drillRefId")), "Drill metadata not found for refId: " + drillMetadataDTO.getRefId());
        List<Challenge> challenges = drillMetadata.getChallenges();
        if (CollectionUtil.isEmpty(drillMetadata.getChallenges())) challenges = new LinkedList<>();
        challenges.add(drillDomainMapper.toEntity(drillMetadata, drillTypes, username));
        drillMetadata.setChallenges(challenges);
        drillMetadata = drillMetadataRepository.save(drillMetadata);
        DrillMetadataDTO response = drillDomainMapper.toDto(drillMetadata);
        log.info("Added challenge. drillRefId={}, totalChallenges={}", response.getRefId(), response.getChallengeDTOList() == null ? 0 : response.getChallengeDTOList().size());
        return response;
    }

    @Override
    public List<ChallengeDTO> getChallengesByDrillRefId(long drillRefId) {
        log.info("Fetching challenges by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = requireEntity(drillMetadataRepository.findByRefId(drillRefId), "Drill metadata not found for refId: " + drillRefId);
        List<ChallengeDTO> response = CollectionUtil.isNotEmpty(drillMetadata.getChallenges()) ? drillMetadata.getChallenges().stream().map(drillDomainMapper::toDto).collect(Collectors.toList()) : new LinkedList<>();
        log.info("Fetched challenges by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public List<ChallengeDTO> getChallengesByDrillRefIdAndUsername(long drillRefId, String username) {
        log.info("Fetching challenges by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = requireEntity(drillMetadataRepository.findByRefId(drillRefId), "Drill metadata not found for refId: " + drillRefId);
        List<Challenge> challenges =drillChallengeRepository.findByDrillIdAndUsernameIgnoreCase(drillMetadata,username);
        List<ChallengeDTO> response = CollectionUtil.isNotEmpty(challenges) ? challenges.stream().map(drillDomainMapper::toDto).toList() : new LinkedList<>();
        log.info("Fetched challenges by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public void deleteChallenge(long drillRefId) {
        log.info("Deleting challenge. drillRefId={}", drillRefId);
        Challenge challenge = requireEntity(drillChallengeRepository.findByRefId(drillRefId), "Drill challenge not found for refId: " + drillRefId);
        List<ChallengeEvaluation> challengeEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(challenge.getChallengeScoresList());
        drillEvaluationRepository.deleteAll(challengeEvaluations);
        drillChallengeRepository.delete(challenge);
        log.info("Deleted challenge. drillRefId={}", drillRefId);
    }

    @Override
    public List<EvaluatorDTO> getEvaluatorsByChallengeId(long challengeRefId) {
        log.info("Fetching evaluators by challengeRefId={}", challengeRefId);
        Challenge challenge = requireEntity(drillChallengeRepository.findByRefId(challengeRefId), "Drill challenge not found for refId: " + challengeRefId);
        List<EvaluatorDTO> response = evaluatorService.getByDrillType(challenge.getChallengeType());
        log.info("Fetched evaluators by challengeRefId={}, count={}", challengeRefId, response.size());
        return response;
    }
}
