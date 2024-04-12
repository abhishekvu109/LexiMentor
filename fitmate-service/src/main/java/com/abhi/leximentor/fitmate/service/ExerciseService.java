package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.ExcerciseDTO;

import java.util.List;

public interface ExerciseService {
    List<ExcerciseDTO> addAll(List<ExcerciseDTO> excerciseDTOS);

    ExcerciseDTO getByRefId(long refId);

    List<ExcerciseDTO> getAllByRefId(List<Long> refIds);

    ExcerciseDTO getByName(String name);

    void update(ExcerciseDTO excerciseDTO);

    void delete(ExcerciseDTO excerciseDTO);

    void deleteAll(List<ExcerciseDTO> excerciseDTOS);
}
