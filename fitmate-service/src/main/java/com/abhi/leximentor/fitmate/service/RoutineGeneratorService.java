package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.RoutineDTO;

import java.util.List;

public interface RoutineGeneratorService {
    RoutineDTO generateRoutine(String trainingType, List<String> targetBodyParts);
}