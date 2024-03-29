package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;

import java.util.List;

public interface DrillSetService {
    public DrillSetDTO getDrillSetByRefId(long refId);

    public DrillSetDTO getDrillSetByDrillSetId(long drillSetId);

    public List<DrillSetDTO> getDrillSetsByDrillId(long drillRefId);

    public List<WordDTO> getWordDataFromDrillId(long drillRefId);
}
