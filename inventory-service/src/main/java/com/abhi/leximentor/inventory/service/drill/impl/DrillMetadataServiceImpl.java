package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import com.abhi.leximentor.inventory.util.ApplicationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillMetadataServiceImpl implements DrillMetadataService {

    private final DrillMetadataRepository drillMetadataRepository;
    private final WordMetadataRepository wordMetadataRepository;
    private final ApplicationUtil applicationUtil;
    private final DrillServiceUtil drillServiceUtil;

    @Override
    @Transactional
    public DrillMetadataDTO createDrillRandomly(int size) {
        if (size < 20) throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyInLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillFromNewWords(int size) {
        if (size < 20) throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyNewWordsLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillFromExistingWords(int size) {
        if (size < 20) throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = wordMetadataRepository.findAllRandomlyExistingWordsLimit(size);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public DrillMetadataDTO createDrillBySource(int size, String source, boolean isNewWords) {
        if (size < 20) throw new IllegalArgumentException("The size of the drill should be at least 20");
        List<WordMetadata> wordMetadataList = (isNewWords) ? wordMetadataRepository.findAllRandomlyNewWordsFromSourceInLimit(size, source) : wordMetadataRepository.findAllRandomlyExistingWordsFromSourceInLimit(size, source);
        return getEntity(size, wordMetadataList);
    }

    @Override
    public List<DrillMetadataDTO> getDrills() {
        List<DrillMetadata> drillMetadataList = drillMetadataRepository.findAll();
        return drillMetadataList.stream().map(DrillServiceUtil.DrillMetadataUtil::buildDTO).toList();
    }

    private DrillMetadataDTO getEntity(int size, List<WordMetadata> wordMetadataList) {
        DrillMetadata drillMetadata = DrillServiceUtil.DrillMetadataUtil.buildEntity(wordMetadataList, applicationUtil);
        drillMetadata = drillMetadataRepository.save(drillMetadata);
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }

    @Override
    public void deleteByRefId(long refId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(refId);
        drillMetadataRepository.delete(drillMetadata);
        log.info("The entity has been deleted: {}", drillMetadata.getName());
    }

    @Override
    public DrillMetadataDTO getByRefId(long refId) {
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(refId);
        return DrillServiceUtil.DrillMetadataUtil.buildDTO(drillMetadata);
    }
}
