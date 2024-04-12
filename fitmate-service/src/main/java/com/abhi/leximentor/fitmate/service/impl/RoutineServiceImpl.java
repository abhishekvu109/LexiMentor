package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.RoutineRepository;
import com.abhi.leximentor.fitmate.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoutineServiceImpl implements RoutineService {
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public List<RoutineDTO> addAll(List<RoutineDTO> routineDTOS) {
        List<Routine> routines = routineDTOS.stream().map(dto -> FitmateServiceUtil.RoutineUtil.buildEntity(dto, exerciseRepository.findByRefId(Long.parseLong(dto.getExcerciseDTO().getRefId())))).toList();
        return routineRepository.saveAll(routines).stream().map(FitmateServiceUtil.RoutineUtil::buildDto).toList();
    }

    @Override
    public RoutineDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Routine routine = routineRepository.findByRefId(refId);
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.RoutineUtil.buildDto(routine);
    }

    @Override
    public List<RoutineDTO> getAllByRefId(List<Long> refIds) throws ServerException.InternalError, ServerException.EntityObjectNotFound {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Routine> entities = routineRepository.findByRefIdIn(refIds);
        if (entities.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return entities.stream().map(FitmateServiceUtil.RoutineUtil::buildDto).toList();
    }


    @Override
    public RoutineDTO update(RoutineDTO routineDTO) throws ServerException.EntityObjectNotFound {
        Routine routine = routineRepository.findByRefId(Long.parseLong(routineDTO.getRefId()));
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        routine.setDescription(routineDTO.getDescription());
        routine.setCompleted(routineDTO.isCompleted());
        routine.setCompletionUnit(routineDTO.getCompletionUnit());
        routine.setMeasurement(routineDTO.getMeasurement());
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(routineDTO.getExcerciseDTO().getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        routine.setExcercise(exercise);
        return FitmateServiceUtil.RoutineUtil.buildDto(routineRepository.save(routine));
    }

    @Override
    public void delete(RoutineDTO routineDTO) throws ServerException.EntityObjectNotFound {
        Routine routine = routineRepository.findByRefId(Long.parseLong(routineDTO.getRefId()));
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        routineRepository.delete(routine);
    }

    @Override
    public void deleteAll(List<RoutineDTO> routineDTOS) throws ServerException.InternalError {
        if (CollectionUtils.isEmpty(routineDTOS))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Routine> routines = routineDTOS.stream().map(dto -> FitmateServiceUtil.RoutineUtil.buildEntity(dto, exerciseRepository.findByRefId(Long.parseLong(dto.getExcerciseDTO().getRefId())))).toList();
        routineRepository.deleteAll(routines);
    }
}
