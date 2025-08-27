package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.TrainingDTO;

import java.util.List;

public interface TrainingService {

    List<TrainingDTO> addAll(List<TrainingDTO> trainingDTOS);

    List<TrainingDTO> getAll();

    TrainingDTO getByRefId(long refId);

    List<TrainingDTO> getAllByRefId(List<Long> refIds);

    TrainingDTO getByName(String name);

    TrainingDTO update(TrainingDTO trainingDTO);

    void delete(TrainingDTO trainingDTO);

    void deleteAll(List<TrainingDTO> trainingDTOS);
}
