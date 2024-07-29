package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeScoreRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeService;
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
    private final DrillChallengeScoreRepository drillChallengeScoreRepository;

    @Override
    @Transactional
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(Long.parseLong(drillMetadataDTO.getRefId()));
        List<DrillChallenge> drillChallenges = drillMetadata.getDrillChallenges();
        if (CollectionUtil.isEmpty(drillMetadata.getDrillChallenges())) drillChallenges = new LinkedList<>();
        drillChallenges.add(DrillServiceUtil.DrillChallengeUtil.buildEntity(drillMetadata, drillTypes));
        drillMetadata.setDrillChallenges(drillChallenges);
        drillMetadata = drillMetadataRepository.save(drillMetadata);
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }

    @Override
    public List<DrillChallengeDTO> getChallengesByDrillRefId(long drillRefId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        return CollectionUtil.isNotEmpty(drillMetadata.getDrillChallenges()) ? drillMetadata.getDrillChallenges().stream().map(d -> DrillServiceUtil.DrillChallengeUtil.buildDTO(d, drillMetadata)).collect(Collectors.toList()) : new LinkedList<>();
    }

    @Override
    public void deleteChallenge(long drillRefId) {
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(drillRefId);
        List<DrillChallengeScores> drillChallengeScores = drillChallengeScoreRepository.findByChallengeId(drillChallenge);
        drillChallengeScoreRepository.deleteAll(drillChallengeScores);
        drillChallengeRepository.delete(drillChallenge);
    }
}
