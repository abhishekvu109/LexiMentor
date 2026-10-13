package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DrillService {
    List<DrillDTO> findAll();

    List<DrillDTO> findByExerciseOrderByCrtnDate(Exercise exercise);

    List<DrillDTO> findByExerciseNameOrderByCrtnDate(String exerciseName);

    PagedResponse<DrillDTO> findByExerciseNameOrderByCrtnDate(String exerciseName, Pageable pageable);

    DrillDTO update(DrillDTO drillDTO);

    void delete(DrillDTO drillDTO);

    DrillDTO add(DrillDTO dto);
}
