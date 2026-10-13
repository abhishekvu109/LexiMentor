package com.abhi.leximentor.leximentor.service.drill.impl;

import com.abhi.leximentor.leximentor.dto.drill.DrillSetDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.mapper.DrillDomainMapper;
import com.abhi.leximentor.leximentor.mapper.InventoryDomainMapper;
import com.abhi.leximentor.leximentor.repository.drill.DrillRepository;
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
    private final DrillRepository drillRepository;
    private final DrillDomainMapper drillDomainMapper;
    private final InventoryDomainMapper inventoryDomainMapper;

    @Override
    public DrillSetDTO getDrillSetByKey(String drillKey) {
        log.info("Fetching drill set by drillKey={}", drillKey);
        DrillSet drillSet = requireEntity(drillSetRepository.findByKey(drillKey).orElse(null), "Drill set not found for refId: " + drillKey);
        DrillSetDTO response = drillDomainMapper.toDto(drillSet);
        log.info("Fetched drill set by key={}", drillKey);
        return response;
    }

    @Override
    public List<DrillSetDTO> getDrillSetsByDrillId(String drillKey) {
        log.info("Fetching drill sets by drill key={}", drillKey);
        Drill drill = requireEntity(drillRepository.findByKey(drillKey).orElse(null), "Drill metadata not found for key: " + drillKey);
        List<DrillSetDTO> response = drillSetRepository.findByDrill(drill).stream().map(drillDomainMapper::toDto).collect(Collectors.toList());
        log.info("Fetched drill sets by drillKey={}, count={}", drillKey, response.size());
        return response;
    }

    @Override
    public List<WordDTO> getWordDataFromDrillId(String drillKey) {
        log.info("Fetching word data by drillKey={}", drillKey);
        Drill drill = requireEntity(drillRepository.findByKey(drillKey).orElse(null), "Drill metadata not found for key: " + drillKey);
        List<DrillSet> drillSetList = drillSetRepository.findByDrill(drill);
        List<WordDTO> response = drillSetList.stream().map(set -> inventoryDomainMapper.toDto(set.getWord())).toList();
        log.info("Fetched word data by drillKey={}, count={}", drillKey, response.size());
        return response;
    }
}
