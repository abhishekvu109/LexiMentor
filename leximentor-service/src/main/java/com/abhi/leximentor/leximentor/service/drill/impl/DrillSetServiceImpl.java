package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.dto.drill.DrillSetDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.mapper.InventoryDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillMetadataRepository;
import com.abhi.leximentor.leximentor.repository.drill.DrillSetRepository;
import com.abhi.leximentor.leximentor.service.base.AbstractApplicationService;
import com.abhi.leximentor.leximentor.service.drill.DrillSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillSetServiceImpl extends AbstractApplicationService implements DrillSetService {
    private final DrillSetRepository drillSetRepository;
    private final DrillMetadataRepository drillMetadataRepository;
    private final DrillDomainMapper drillDomainMapper;
    private final InventoryDomainMapper inventoryDomainMapper;

    @Override
    public DrillSetDTO getDrillSetByRefId(long refId) {
        log.info("Fetching drill set by refId={}", refId);
        DrillSet drillSet = requireEntity(drillSetRepository.findByRefId(refId), "Drill set not found for refId: " + refId);
        DrillSetDTO response = drillDomainMapper.toDto(drillSet);
        log.info("Fetched drill set by refId={}", refId);
        return response;
    }

    @Override
    public DrillSetDTO getDrillSetByDrillSetId(long drillSetId) {
        log.info("Fetching drill set by id={}", drillSetId);
        DrillSet drillSet = requireEntity(drillSetRepository.findById(drillSetId).orElse(null), "Drill set not found for id: " + drillSetId);
        DrillSetDTO response = drillDomainMapper.toDto(drillSet);
        log.info("Fetched drill set by id={}", drillSetId);
        return response;
    }

    @Override
    public List<DrillSetDTO> getDrillSetsByDrillId(long drillRefId) {
        log.info("Fetching drill sets by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = requireEntity(drillMetadataRepository.findByRefId(drillRefId), "Drill metadata not found for refId: " + drillRefId);
        List<DrillSetDTO> response = drillSetRepository.findDrillSetByDrillId(drillMetadata).stream().map(drillDomainMapper::toDto).collect(Collectors.toList());
        log.info("Fetched drill sets by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }

    @Override
    public List<WordDTO> getWordDataFromDrillId(long drillRefId) {
        log.info("Fetching word data by drillRefId={}", drillRefId);
        DrillMetadata drillMetadata = requireEntity(drillMetadataRepository.findByRefId(drillRefId), "Drill metadata not found for refId: " + drillRefId);
        List<DrillSet> drillSetList = drillSetRepository.findDrillSetByDrillId(drillMetadata);
        List<WordDTO> response = drillSetList.stream().map(set -> inventoryDomainMapper.toDto(set.getWordId())).toList();
        log.info("Fetched word data by drillRefId={}, count={}", drillRefId, response.size());
        return response;
    }
}
