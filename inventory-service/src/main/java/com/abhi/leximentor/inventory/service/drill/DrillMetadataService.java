package com.abhi.leximentor.inventory.service.drill;

import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;

public interface DrillMetadataService {
    public DrillMetadataDTO createDrillRandomly(int size);

    public DrillMetadataDTO createDrillFromNewWords(int size);

    public DrillMetadataDTO createDrillFromExistingWords(int size);

    public DrillMetadataDTO createDrillBySource(int size, String source, boolean isNewWords);

    public DrillMetadataDTO getDrills();

    public void deleteByRefId(String refId);
    public DrillMetadataDTO getByRefId(String refId);
}
