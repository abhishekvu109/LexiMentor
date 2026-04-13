package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.service.drill.DrillSetService;
import com.abhi.leximentor.inventory.service.inv.impl.InventoryServiceUtil;
import com.abhi.leximentor.inventory.service.inv.impl.WordServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillSetServiceImpl implements DrillSetService {
    private final DrillSetRepository drillSetRepository;
    private final DrillMetadataRepository drillMetadataRepository;

    @Override
    public DrillSetDTO getDrillSetByRefId(long refId) {
        log.info("Fetching drill set by refId={}", refId);
        DrillSet drillSet = drillSetRepository.findByRefId(refId);
        DrillSetDTO response = DrillServiceUtil.DrillSetUtil.buildDTO(drillSet);
        log.info("Fetched drill set by refId={}", refId);
        return response;
    }

    @Override
    public DrillSetDTO getDrillSetByDrillSetId(long drillSetId) {
        log.info("Fetching drill set by id={}", drillSetId);
        Optional<DrillSet> drillSet = drillSetRepository.findById(drillSetId);
        if (drillSet.isEmpty()) {
            log.error("Unable to find the Drill set entity");
            throw new RuntimeException("Unable to find the Drill set entity");
        }
        DrillSetDTO response = DrillServiceUtil.DrillSetUtil.buildDTO(drillSet.get());
        log.info("Fetched drill set by id={}", drillSetId);
        return response;
    }

    @Override
    public List<DrillSetDTO> getDrillSetsByDrillId(long drillRefId) {
        log.info("Fetching drill sets by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        List<DrillSetDTO> response = drillSetRepository.findDrillSetByDrillId(drillMetadata).stream().map(DrillServiceUtil.DrillSetUtil::buildDTO).collect(Collectors.toList());
        log.info("Fetched drill sets by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public List<WordDTO> getWordDataFromDrillId(long drillRefId) {
        log.info("Fetching word data by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = drillMetadataRepository.findByRefId(drillRefId);
        List<DrillSet> drillSetList = drillSetRepository.findDrillSetByDrillId(drillMetadata);
        List<WordDTO> response = drillSetList.stream().map(set -> InventoryServiceUtil.WordMetadataUtil.buildDTO(set.getWordId())).toList();
        log.info("Fetched word data by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }
}
