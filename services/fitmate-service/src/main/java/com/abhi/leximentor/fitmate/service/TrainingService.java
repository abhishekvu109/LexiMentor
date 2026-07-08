package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.dto.TrainingDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingService {

    List<TrainingDTO> addAll(List<TrainingDTO> trainingDTOS);

    List<TrainingDTO> getAll();

    PagedResponse<TrainingDTO> getAll(Pageable pageable);

    TrainingDTO getByRefId(long refId);

    List<TrainingDTO> getAllByRefId(List<Long> refIds);

    TrainingDTO getByName(String name);

    TrainingDTO update(TrainingDTO trainingDTO);

    void delete(TrainingDTO trainingDTO);

    void deleteAll(List<TrainingDTO> trainingDTOS);
}
