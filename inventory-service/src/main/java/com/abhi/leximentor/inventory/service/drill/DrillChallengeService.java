package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;

import java.util.List;

public interface DrillChallengeService {
    public DrillMetadataDTO addChallenges(DrillMetadataDTO drillMetadataDTO, DrillTypes drillTypes);

    public List<DrillChallengeDTO> getChallengesByDrillRefId(long drillRefId);
}
