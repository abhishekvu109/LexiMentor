package com.abhi.leximentor.inventory.service.drill.impl;

import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.repository.drill.DrillSetRepository;
import com.abhi.leximentor.inventory.service.drill.DrillSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillSetServiceImpl implements DrillSetService {
    private final DrillSetRepository drillSetRepository;

    @Override
    public DrillSetDTO getDrillSetByRefId(long refId) {
        DrillSet drillSet = drillSetRepository.findByRefId(refId);
        return DrillServiceUtil.DrillSetUtil.buildDTO(drillSet);
    }

    @Override
    public DrillSetDTO getDrillSetByDrillId(long drillId) {
        Optional<DrillSet> drillSet = drillSetRepository.findById(drillId);
        if (drillSet.isEmpty()) {
            log.error("Unable to find the Drill set entity");
            throw new RuntimeException("Unable to find the Drill set entity");
        }
        return DrillServiceUtil.DrillSetUtil.buildDTO(drillSet.get());
    }
}
