package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.dto.drill.DrillSetDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;

import java.util.List;

public interface DrillSetService {
    DrillSetDTO getDrillSetByKey(String drillSetKey);


    List<DrillSetDTO> getDrillSetsByDrillId(String drillKey);

    List<WordDTO> getWordDataFromDrillId(String drillKey);
}
