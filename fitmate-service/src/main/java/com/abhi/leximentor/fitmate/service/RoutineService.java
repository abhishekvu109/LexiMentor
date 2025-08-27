package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineSearchFilter;

import java.util.List;

public interface RoutineService {

    RoutineDTO add(RoutineDTO routineDTO);

    RoutineDTO addByNames(RoutineDTO routineDTO);

    RoutineDTO getByRefId(long refId);

    List<RoutineDTO> getAllByRefId(List<Long> refIds);


    RoutineDTO update(RoutineDTO routineDTO);

    void delete(RoutineDTO routineDTO);

    void deleteAll(List<RoutineDTO> routineDTOS);

    List<RoutineDTO> search(RoutineSearchFilter filter);
}
