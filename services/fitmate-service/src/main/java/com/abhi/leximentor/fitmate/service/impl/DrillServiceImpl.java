package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.DrillMapper;
import org.springframework.data.domain.Pageable;
import com.abhi.leximentor.fitmate.repository.DrillRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.service.DrillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillServiceImpl implements DrillService {

    private final DrillRepository drillRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final DrillMapper drillMapper;

    @Override
    public List<DrillDTO> findAll() {
        return drillRepository.findAll().stream()
                .map(drillMapper::toDto)
                .toList();
    }

    @Override
    public List<DrillDTO> findByExerciseOrderByCrtnDate(Exercise exercise) {
        return drillRepository.findByExerciseOrderByCrtnDateDesc(exercise).stream()
                .map(drillMapper::toDto)
                .toList();
    }

    @Override
    public List<DrillDTO> findByExerciseNameOrderByCrtnDate(String exerciseName) {
        Exercise exercise = exerciseRepository.findByName(exerciseName.toUpperCase());
        if (exercise == null)
            throw new ServerException().new InternalError("Exercise is not found.");
        return findByExerciseOrderByCrtnDate(exercise);
    }

    @Override
    public PagedResponse<DrillDTO> findByExerciseNameOrderByCrtnDate(String exerciseName, Pageable pageable) {
        Exercise exercise = exerciseRepository.findByName(exerciseName.toUpperCase());
        if (exercise == null)
            throw new ServerException().new InternalError("Exercise is not found.");
        return PagedResponse.of(
                drillRepository.findByExerciseOrderByCrtnDateDesc(exercise, pageable),
                drillMapper::toDto);
    }

    @Override
    @Transactional
    public DrillDTO update(DrillDTO drillDTO) {
        Drill drill = drillRepository.findByRefId(Long.parseLong(drillDTO.getRefId()));
        if (drill == null)
            throw new ServerException().new EntityObjectNotFound("Drill object is not found.");
        drill.setUnit(drillDTO.getUnit());
        drill.setMeasurement(drillDTO.getMeasurement());
        drill.setRepetition(drillDTO.getRepetition());
        drill.setBurntCalories(drillDTO.getBurntCalories());
        return drillMapper.toDto(drillRepository.save(drill));
    }

    @Override
    @Transactional
    public void delete(DrillDTO drillDTO) {
        Drill drill = drillRepository.findByRefId(Long.parseLong(drillDTO.getRefId()));
        if (drill == null)
            throw new ServerException().new EntityObjectNotFound("Drill object is not found.");
        Routine routine = drill.getRoutineObj();
        if (routine == null && drill.getRoutine() > 0) {
            routine = routineRepository.findByRefId(drill.getRoutine());
        }
        if (routine != null && routine.getDrills() != null) {
            routine.getDrills().removeIf(existing -> existing.getRefId() == drill.getRefId());
            routineRepository.save(routine);
        }
        drillRepository.delete(drill);
    }

    @Override
    @Transactional
    public DrillDTO add(DrillDTO dto) {
        if (StringUtils.isAnyEmpty(dto.getRoutine(), dto.getExercise().getRefId()))
            throw new ServerException().new InternalError("Routine/Exercise can't be empty");
        Routine routine = routineRepository.findByRefId(Long.parseLong(dto.getRoutine()));
        if (routine == null)
            throw new ServerException().new EntityObjectNotFound("Routine object is not found");
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getExercise().getRefId()));
        Drill drill = drillMapper.toEntity(dto);
        drill.setRoutineObj(routine);
        drill.setRoutine(routine.getRefId());
        drill.setExercise(exercise);
        drill = drillRepository.save(drill);
        return drillMapper.toDto(drill);
    }
}
