package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.RoutineDTO;

import java.util.List;

public interface RoutineService {

    List<RoutineDTO> addAll(List<RoutineDTO> routineDTOS);

    RoutineDTO getByRefId(long refId);

    List<RoutineDTO> getAllByRefId(List<Long> refIds);

    RoutineDTO getByName(String name);

    void update(RoutineDTO routineDTO);

    void delete(RoutineDTO routineDTO);

    void deleteAll(List<RoutineDTO> routineDTOS);
}
