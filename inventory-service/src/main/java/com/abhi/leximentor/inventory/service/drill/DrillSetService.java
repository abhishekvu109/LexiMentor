package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;

public interface DrillSetService {
    public DrillSetDTO getDrillSetByRefId(long refId);

    public DrillSetDTO getDrillSetByDrillId(long drillId);

}
