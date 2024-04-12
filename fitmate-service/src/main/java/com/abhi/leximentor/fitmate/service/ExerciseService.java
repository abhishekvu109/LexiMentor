package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;

import java.util.List;

public interface ExerciseService {
    List<ExerciseDTO> addAll(List<ExerciseDTO> excerciseDTOS);

    ExerciseDTO getByRefId(long refId);

    List<ExerciseDTO> getAllByRefId(List<Long> refIds);

    ExerciseDTO getByName(String name);

    ExerciseDTO update(ExerciseDTO excerciseDTO);

    void delete(ExerciseDTO excerciseDTO);

    void deleteAll(List<ExerciseDTO> excerciseDTOS);
}
