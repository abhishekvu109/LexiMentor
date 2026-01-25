package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineSearchFilter;
import com.abhi.leximentor.fitmate.entities.*;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.*;
import com.abhi.leximentor.fitmate.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoutineServiceImpl implements RoutineService {
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;
    private final DrillRepository drillRepository;
    private final MuscleRepository muscleRepository;

    @Override
    @Transactional
    public RoutineDTO add(RoutineDTO routineDTO) {
        Training training = trainingRepository.findByRefId(Long.parseLong(routineDTO.getTraining().getRefId()));
        Routine routine = FitmateServiceUtil.RoutineUtil.buildEntity(routineDTO, training);
        List<Drill> drills = new LinkedList<>();
        routineDTO.getDrills().forEach(dto -> {
            Drill drill = FitmateServiceUtil.DrillUtil.buildEntity(dto);
            Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getExercise().getRefId()));
            if (exercise == null) {
                throw new ServerException().new InternalError("The exercise object is not found.");
            }
//            Muscle muscle = muscleRepository.findByRefId(Long.parseLong(dto.getMuscle().getRefId()));
//            if (muscle == null) {
//                throw new ServerException().new InternalError("The muscle object is not found.");
//            }
            drill.setExercise(exercise);
//            drill.setMuscle(muscle);
            drill.setRoutine(routine.getRefId());
            drills.add(drill);
        });
        routine.setDrills(drills);
        return FitmateServiceUtil.RoutineUtil.buildDto(routineRepository.save(routine));
    }

    @Override
    @Transactional
    public RoutineDTO addByNames(RoutineDTO routineDTO) {
        Training training = trainingRepository.findByName(routineDTO.getTraining().getName());
        Routine routine = FitmateServiceUtil.RoutineUtil.buildEntity(routineDTO, training);
        List<Drill> drills = new LinkedList<>();
        routineDTO.getDrills().forEach(dto -> {
            Drill drill = FitmateServiceUtil.DrillUtil.buildEntity(dto);
            Exercise exercise = exerciseRepository.findByName(dto.getExercise().getName());
            if (exercise == null) {
                throw new ServerException().new InternalError("The exercise object is not found.");
            }
//            Muscle muscle = muscleRepository.findByName(dto.getMuscle().getName());
//            if (muscle == null) {
//                throw new ServerException().new InternalError("The muscle object is not found.");
//            }
            drill.setExercise(exercise);
//            drill.setMuscle(muscle);
            drill.setRoutine(routine.getRefId());
            drills.add(drill);
        });
        routine.setDrills(drills);
        return FitmateServiceUtil.RoutineUtil.buildDto(routineRepository.save(routine));
    }

    @Override
    public RoutineDTO getByRefId(long refId) {
        long routineRefId = refId;
        Routine routine = routineRepository.findByRefId(routineRefId);
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
        routine.setDrills(drills);
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
    @Transactional
    public RoutineDTO update(RoutineDTO routineDTO) throws ServerException.EntityObjectNotFound {
        Routine routine = routineRepository.findByRefId(Long.parseLong(routineDTO.getRefId()));
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        routine.setDescription(routineDTO.getDescription());
        routine.setStatus(Status.RoutineStatus.toInt(routineDTO.getStatus()));
        routine.setDurationInMinutes(routineDTO.getDurationInMinutes());
        routine.setBurntCalories(routineDTO.getBurntCalories());
        List<Drill> drills = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(routineDTO.getDrills())) {
            routineDTO.getDrills().forEach(dto -> {
                Drill drill = StringUtils.isNotEmpty(dto.getRefId())
                        ? drillRepository.findByRefId(Long.parseLong(dto.getRefId()))
                        : FitmateServiceUtil.DrillUtil.buildEntity(dto);
                Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getExercise().getRefId()));
                if (exercise == null) {
                    throw new ServerException().new InternalError("The exercise object is not found.");
                }
                drill.setExercise(exercise);
                drill.setRoutine(routine.getRefId());
                drills.add(drill);
            });
            routine.setDrills(drills);
        }
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
        List<Routine> routines = routineRepository.findByRefIdIn(routineDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList());
        routineRepository.deleteAll(routines);
    }

    @Override
    public List<RoutineDTO> search(RoutineSearchFilter filter) {
        Specification<Routine> specification = Specification.where(null);
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), Status.RoutineStatus.toInt(filter.getStatus()))));
        }
        if (StringUtils.isNotEmpty(filter.getRefId())) {
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("refId"), filter.getRefId())));
        }
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        return routineRepository.findAll(specification, sort).stream()
                .map(routine -> {
                    List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
                    List<DrillDTO> dtoList = CollectionUtils.isEmpty(drills) ? Collections.emptyList() :
                            drills.stream().map(FitmateServiceUtil.DrillUtil::buildDTO).toList();
                    return FitmateServiceUtil.RoutineUtil.buildDto(routine, dtoList);
                }).toList();
    }
}
