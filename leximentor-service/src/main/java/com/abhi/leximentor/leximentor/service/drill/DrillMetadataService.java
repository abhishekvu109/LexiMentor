package com.abhi.leximentor.leximentor.service.drill;

import com.abhi.leximentor.leximentor.dto.drill.DrillMetadataDTO;

import java.util.Collection;
import java.util.List;

public interface DrillMetadataService {
    public DrillMetadataDTO createDrillRandomly(int size);

    public DrillMetadataDTO createDrillFromNewWords(int size);

    public DrillMetadataDTO createDrillFromExistingWords(int size);

    public DrillMetadataDTO createDrillBySource(int size, String source, boolean isNewWords);

    public List<DrillMetadataDTO> getDrills();

    public void deleteByRefId(long refId);

    public DrillMetadataDTO getByRefId(long refId);

    public Collection<String> getWordsInStrByDrillRefId(long drillRefId);

    public DrillMetadataDTO assignDrillName(long drillRefId);
}
