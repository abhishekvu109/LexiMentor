package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;
import com.abhi.leximentor.leximentor.entities.NamedObject;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.repository.NamedObjectRepository;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeEvaluationRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.drill.DrillService;
import com.abhi.leximentor.leximentor.util.ApplicationUtil;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class DrillServiceImpl implements DrillService {

    private final DrillRepository drillRepository;
    private final WordMetadataRepository wordMetadataRepository;
    private final ApplicationUtil applicationUtil;
    private final ChallengeEvaluationRepository challengeEvaluationRepository;
    private final NamedObjectRepository namedObjectRepository;
    private final DrillDomainMapper drillDomainMapper;

    @Override
    @Transactional
    public DrillDTO createDrillRandomly(int size) {
        log.info("Creating drill randomly. size={}", size);
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyInLimit(size);
        DrillDTO response = getEntity(wordMetadataList);
        log.info("Created drill randomly. drillKey={}", response.getKey());
        return response;
    }

    @Override
    public DrillDTO createDrillFromNewWords(int size) {
        log.info("Creating drill from new words. size={}", size);
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyNewWordsLimit(size);
        DrillDTO response = getEntity(wordMetadataList);
        log.info("Created drill from new words. drillKey={}", response.getKey());
        return response;
    }

    @Override
    public DrillDTO createDrillFromExistingWords(int size) {
        log.info("Creating drill from existing words. size={}", size);
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyExistingWordsLimit(size);
        DrillDTO response = getEntity(wordMetadataList);
        log.info("Created drill from existing words. drillKey={}", response.getKey());
        return response;
    }

    @Override
    public DrillDTO createDrillBySource(int size, String source, boolean isNewWords) {
        log.info("Creating drill by source. size={}, source={}, isNewWords={}", size, source, isNewWords);
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = (isNewWords) ? wordMetadataRepository.findAllRandomlyNewWordsFromSourceInLimit(size, source) : wordMetadataRepository.findAllRandomlyExistingWordsFromSourceInLimit(size, source);
        DrillDTO response = getEntity(wordMetadataList);
        log.info("Created drill by source. drillKey={}", response.getKey());
        return response;
    }

    @Override
    public List<DrillDTO> getDrills() {
        log.info("Fetching drills");
        List<Drill> drillList = drillRepository.findAll();
        if (CollectionUtil.isNotEmpty(drillList))
            return drillList.stream().map(drillDomainMapper::toDto).toList();
        return new LinkedList<>();
    }

    private DrillDTO getEntity(List<WordMetadata> wordMetadataList) {
        log.info("Building drill metadata entity. wordCount={}", wordMetadataList == null ? 0 : wordMetadataList.size());
        Drill drill = drillDomainMapper.toEntity(wordMetadataList, applicationUtil);
        drill = drillRepository.save(drill);
        DrillDTO response = drillDomainMapper.toDto(drill);
        log.info("Built drill metadata entity. key={}", response.getKey());
        return response;
    }

    @Override
    @Transactional
    public void deleteByKey(String key) {
        log.info("Deleting drill metadata. key={}", key);
        Drill drill = drillRepository.findByKey(key).orElseThrow(() -> new EntityNotFoundException("Drill object not found key: " + key));
        List<Challenge> challenges = drill.getChallenges();
        List<ChallengeScores> challengeScores = new LinkedList<>();
        for (Challenge challenge : challenges)
            challengeScores.addAll(challenge.getChallengeScoresList());
        List<ChallengeEvaluation> challengeEvaluations = challengeEvaluationRepository.findByChallengeScoresIn(challengeScores);
        challengeEvaluationRepository.deleteAll(challengeEvaluations);
        log.info("Removed the drill evaluations");
        drillRepository.delete(drill);
        log.info("The entity has been deleted: {}", drill.getName());
    }


    @Override
    public DrillDTO getByKey(String key) {
        log.info("Fetching drill metadata. key={}", key);
        Drill drill = drillRepository.findByKey(key).orElseThrow(() -> new EntityNotFoundException("Drill object not found, Key:" + key));
        DrillDTO response = drillDomainMapper.toDto(drill);
        log.info("Fetched drill metadata. key={}", key);
        return response;
    }

    @Override
    public Collection<String> getWordsInStrByDrillKey(String key) {
        log.info("Fetching words in drill. drillKey={}", key);
        Drill drill = drillRepository.findByKey(key).orElseThrow(() -> new EntityNotFoundException("Drill not found, Key:" + key));
        Collection<String> response = drill.getDrillSetList().stream().map(drillSet -> drillSet.getWord().getWord()).toList();
        log.info("Fetched words in drill. drillKey={}, count={}", key, response.size());
        return response;
    }

    @Override
    @Transactional
    public DrillDTO assignDrillName(String drillKey) {
        log.info("Starting name assignment for drill with key: {}", drillKey);

        Drill drill = drillRepository.findByKey(drillKey).orElseThrow(() -> new EntityNotFoundException("Drill not found, Key:" + drillKey));
        log.info("Retrieved drill metadata [ID: {}] for refId: {}", drill.getId(), drillKey);

        if (drill.getNamedObject() != null) {
            throw new ServerException().new InternalError("Drill already has a name.");
        }

        NamedObject namedObject = namedObjectRepository.get();
        if (namedObject == null) {
            log.error("No active NamedObject found for drill key: {}", drillKey);
            throw new ServerException.NoActiveNameObjectFound("No active Named object found");
        }
        log.info("Acquired NamedObject [ID: {}, Name: {}]", namedObject.getId(), namedObject.getName());

        drill.setNamedObject(namedObject);
        log.info("Assigned name '{}' to drill [ID: {}]", namedObject.getName(), drill.getId());

        drill = drillRepository.save(drill);
        log.info("Updated drill metadata [ID: {}] persisted successfully", drill.getId());

        namedObject.setStatus(Status.ApplicationStatus.INACTIVE);
        log.info("Marking NamedObject [ID: {}] as INACTIVE", namedObject.getId());

        namedObjectRepository.save(namedObject);
        log.info("NamedObject [ID: {}] status update persisted", namedObject.getId());

        log.info("Completed name assignment for drill [ID: {}]", drill.getId());
        return drillDomainMapper.toDto(drill);
    }

}
