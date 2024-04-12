package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.TrainingMetadataDTO;

import java.util.List;

public interface TrainingMetaDataService {

    List<TrainingMetadataDTO> addAll(List<TrainingMetadataDTO> trainingMetadataDTOS);

    TrainingMetadataDTO getByRefId(long refId);

    List<TrainingMetadataDTO> getAllByRefId(List<Long> refIds);

    TrainingMetadataDTO getByName(String name);

    TrainingMetadataDTO update(TrainingMetadataDTO trainingMetadataDTO);

    void delete(TrainingMetadataDTO trainingMetadataDTO);

    void deleteAll(List<TrainingMetadataDTO> trainingMetadataDTOS);
}
