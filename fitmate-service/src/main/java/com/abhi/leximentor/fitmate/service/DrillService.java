package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.entities.Exercise;

import java.util.List;

public interface DrillService {
    List<DrillDTO> findAll();

    List<DrillDTO> findByExerciseOrderByCrtnDate(Exercise exercise);

    List<DrillDTO> findByExerciseNameOrderByCrtnDate(String exerciseName);

    DrillDTO update(DrillDTO drillDTO);
    void delete(DrillDTO drillDTO);
}
