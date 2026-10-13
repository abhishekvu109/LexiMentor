package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;

import java.util.Collection;
import java.util.List;

public interface DrillService {
    DrillDTO createDrillRandomly(int size);

    DrillDTO createDrillFromNewWords(int size);

    DrillDTO createDrillFromExistingWords(int size);

    DrillDTO createDrillBySource(int size, String source, boolean isNewWords);

    List<DrillDTO> getDrills();

    void deleteByKey(String key);

    DrillDTO getByKey(String key);

    Collection<String> getWordsInStrByDrillKey(String drillKey);

    DrillDTO assignDrillName(String key);
}
