package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineGenerationDTO;

import java.util.List;

public interface RoutineGeneratorService {
    RoutineDTO generateRoutine(RoutineGenerationDTO dto);
}