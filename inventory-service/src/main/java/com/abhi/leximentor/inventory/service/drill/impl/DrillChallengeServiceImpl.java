package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeService;
import com.abhi.leximentor.inventory.service.inv.EvaluatorService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
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
public class DrillChallengeServiceImpl implements DrillChallengeService {

    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillMetadataRepository drillMetadataRepository;
    private final DrillEvaluationRepository drillEvaluationRepository;
    private final EvaluatorService evaluatorService;

    @Override
    @Transactional
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes) {
        log.info("Adding challenge. drillRefId={}, drillType={}", drillMetadataDTO == null ? null : drillMetadataDTO.getRefId(), drillTypes);
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(Long.parseLong(drillMetadataDTO.getRefId()));
        List<DrillChallenge> drillChallenges = drillMetadata.getDrillChallenges();
        if (CollectionUtil.isEmpty(drillMetadata.getDrillChallenges())) drillChallenges = new LinkedList<>();
        drillChallenges.add(DrillServiceUtil.DrillChallengeUtil.buildEntity(drillMetadata, drillTypes));
        drillMetadata.setDrillChallenges(drillChallenges);
        drillMetadata = drillMetadataRepository.save(drillMetadata);
        DrillMetadataDTO response = DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
        log.info("Added challenge. drillRefId={}, totalChallenges={}", response.getRefId(), response.getDrillChallengeDTOList() == null ? 0 : response.getDrillChallengeDTOList().size());
        return response;
    }

    @Override
    public List<DrillChallengeDTO> getChallengesByDrillRefId(long drillRefId) {
        log.info("Fetching challenges by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        List<DrillChallengeDTO> response = CollectionUtil.isNotEmpty(drillMetadata.getDrillChallenges()) ? drillMetadata.getDrillChallenges().stream().map(d -> DrillServiceUtil.DrillChallengeUtil.buildDTO(d)).collect(Collectors.toList()) : new LinkedList<>();
        log.info("Fetched challenges by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public List<DrillChallengeDTO> getChallengesByDrillRefIdAndUsername(long drillRefId, String username) {
        log.info("Fetching challenges by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        List<DrillChallenge> drillChallenges=drillChallengeRepository.findByDrillIdAndUsernameIgnoreCase(drillMetadata,username);
        List<DrillChallengeDTO> response = CollectionUtil.isNotEmpty(drillChallenges) ? drillChallenges.stream().map(DrillServiceUtil.DrillChallengeUtil::buildDTO).toList() : new LinkedList<>();
        log.info("Fetched challenges by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public void deleteChallenge(long drillRefId) {
        log.info("Deleting challenge. drillRefId={}", drillRefId);
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(drillRefId);
        List<DrillEvaluation> drillEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(drillChallenge.getDrillChallengeScoresList());
        drillEvaluationRepository.deleteAll(drillEvaluations);
        drillChallengeRepository.delete(drillChallenge);
        log.info("Deleted challenge. drillRefId={}", drillRefId);
    }

    @Override
    public List<EvaluatorDTO> getEvaluatorsByChallengeId(long challengeRefId) {
        log.info("Fetching evaluators by challengeRefId={}", challengeRefId);
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(challengeRefId);
        List<EvaluatorDTO> response = evaluatorService.getByDrillType(drillChallenge.getDrillType());
        log.info("Fetched evaluators by challengeRefId={}, count={}", challengeRefId, response.size());
        return response;
    }
}
