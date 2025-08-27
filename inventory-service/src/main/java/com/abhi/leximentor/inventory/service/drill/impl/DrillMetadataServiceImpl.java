package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.entities.NamedObject;
import com.abhi.leximentor.inventory.entities.drill.*;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.repository.NamedObjectRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillEvaluationRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import com.abhi.leximentor.inventory.util.ApplicationUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
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
public class DrillMetadataServiceImpl implements DrillMetadataService {

    private final DrillMetadataRepository drillMetadataRepository;
    private final WordMetadataRepository wordMetadataRepository;
    private final ApplicationUtil applicationUtil;
    private final DrillEvaluationRepository drillEvaluationRepository;
    private final NamedObjectRepository namedObjectRepository;

    @Override
    @Transactional
    public DrillMetadataDTO createDrillRandomly(int size) {
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyInLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillFromNewWords(int size) {
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyNewWordsLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillFromExistingWords(int size) {
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyExistingWordsLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillBySource(int size, String source, boolean isNewWords) {
        if (size < ApplicationConstants.MIN_DRILL_SIZE)
            throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = (isNewWords) ? wordMetadataRepository.findAllRandomlyNewWordsFromSourceInLimit(size, source) : wordMetadataRepository.findAllRandomlyExistingWordsFromSourceInLimit(size, source);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public List<DrillMetadataDTO> getDrills() {
        List<DrillMetadata> drillMetadataList = drillMetadataRepository.findAll();
        if (CollectionUtil.isNotEmpty(drillMetadataList))
            return drillMetadataList.stream().map(DrillServiceUtil.DrillMetadataUtil::buildDTO).toList();
        return new LinkedList<DrillMetadataDTO>();
    }

    private DrillMetadataDTO getEntity(int size, List<WordMetadata> wordMetadataList) {
        DrillMetadata drillMetadata = DrillServiceUtil.DrillMetadataUtil.buildEntity(wordMetadataList, applicationUtil);
        drillMetadata = drillMetadataRepository.save(drillMetadata);
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }

    @Override
    @Transactional
    public void deleteByRefId(long refId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(refId);
        List<DrillSet> drillSetList = drillMetadata.getDrillSetList();
        List<DrillChallenge> drillChallenges = drillMetadata.getDrillChallenges();
        List<DrillChallengeScores> drillChallengeScores = new LinkedList<>();
        for (DrillChallenge drillChallenge : drillChallenges)
            drillChallengeScores.addAll(drillChallenge.getDrillChallengeScoresList());
        List<DrillEvaluation> drillEvaluations = drillEvaluationRepository.findByDrillChallengeScoresIn(drillChallengeScores);
        drillEvaluationRepository.deleteAll(drillEvaluations);
        log.info("Removed the drill evaluations");
        drillMetadataRepository.delete(drillMetadata);
        log.info("The entity has been deleted: {}", drillMetadata.getName());
    }


    @Override
    public DrillMetadataDTO getByRefId(long refId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(refId);
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }

    @Override
    public Collection<String> getWordsInStrByDrillRefId(long drillRefId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        return drillMetadata.getDrillSetList().stream().map(drillSet -> drillSet.getWordId().getWord()).toList();
    }

    //    @Override
//    @Transactional
//    public DrillMetadataDTO assignDrillName(long drillRefId) {
//        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
//        NamedObject namedObject = namedObjectRepository.get();
//        if (namedObject == null) throw new ServerException.NoActiveNameObjectFound("No active Named object found");
//        drillMetadata.setNamedObject(namedObject);
//        drillMetadata = drillMetadataRepository.save(drillMetadata);
//        namedObject.setStatus(Status.ApplicationStatus.INACTIVE);
//        namedObjectRepository.save(namedObject);
//        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
//    }
    @Override
    @Transactional
    public DrillMetadataDTO assignDrillName(long drillRefId) {
        log.info("Starting name assignment for drill with refId: {}", drillRefId);

        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        log.info("Retrieved drill metadata [ID: {}] for refId: {}", drillMetadata.getId(), drillRefId);

        if(drillMetadata.getNamedObject()!=null){
            throw new ServerException().new InternalError("Drillmetadata already has a name.");
        }

        NamedObject namedObject = namedObjectRepository.get();
        if (namedObject == null) {
            log.error("No active NamedObject found for drill refId: {}", drillRefId);
            throw new ServerException.NoActiveNameObjectFound("No active Named object found");
        }
        log.info("Acquired NamedObject [ID: {}, Name: {}]", namedObject.getId(), namedObject.getName());

        drillMetadata.setNamedObject(namedObject);
        log.info("Assigned name '{}' to drill [ID: {}]", namedObject.getName(), drillMetadata.getId());

        drillMetadata = drillMetadataRepository.save(drillMetadata);
        log.info("Updated drill metadata [ID: {}] persisted successfully", drillMetadata.getId());

        namedObject.setStatus(Status.ApplicationStatus.INACTIVE);
        log.info("Marking NamedObject [ID: {}] as INACTIVE", namedObject.getId());

        namedObjectRepository.save(namedObject);
        log.info("NamedObject [ID: {}] status update persisted", namedObject.getId());

        log.info("Completed name assignment for drill [ID: {}]", drillMetadata.getId());
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }

}
