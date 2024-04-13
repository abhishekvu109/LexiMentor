package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;

import java.util.List;

public interface ExerciseService {
    List<ExerciseDTO> addAll(List<ExerciseDTO> excerciseDTOS);

    ExerciseDTO getByRefId(long refId);

    List<ExerciseDTO> getByBodyPartRefId(long bodyPartRefId);

    List<ExerciseDTO> getAllByRefId(List<Long> refIds);
    List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadatRefId);
    List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId,long targetBodyPartRefId);

    ExerciseDTO getByName(String name);

    ExerciseDTO update(ExerciseDTO excerciseDTO);

    void delete(ExerciseDTO excerciseDTO);

    void deleteAll(List<ExerciseDTO> excerciseDTOS);
}
