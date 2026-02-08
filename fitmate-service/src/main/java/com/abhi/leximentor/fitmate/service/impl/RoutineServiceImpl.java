package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDrillLog;
import com.abhi.leximentor.fitmate.dto.filters.RoutineSearchFilter;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.entities.Training;
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

import java.sql.Date;
import java.time.LocalDate;
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
            drill.setExercise(exercise);
            drill.setRoutine(routine.getRefId());
            drill.setRoutineObj(routine);
            drills.add(drill);
        });
        routine.setDrills(drills);
        return FitmateServiceUtil.RoutineUtil.buildDto(routineRepository.save(routine));
    }

    @Override
    @Transactional
    public RoutineDTO addByNames(RoutineDTO routineDTO) {
        Training training = trainingRepository.findByNameIgnoreCase(routineDTO.getTraining().getName());
        Routine routine = FitmateServiceUtil.RoutineUtil.buildEntity(routineDTO, training);
        List<Drill> drills = new LinkedList<>();
        routineDTO.getDrills().forEach(dto -> {
            Drill drill = FitmateServiceUtil.DrillUtil.buildEntity(dto);
            Exercise exercise = exerciseRepository.findByName(dto.getExercise().getName());
            if (exercise == null) {
                throw new ServerException().new InternalError("The exercise object is not found.");
            }
            drill.setExercise(exercise);
            drill.setRoutine(routine.getRefId());
            drill.setRoutineObj(routine);
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
                drill.setRoutineObj(routine);
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
        Specification<Routine> spec = Specification.where(null);
        if (filter == null || filter.isEmpty()) {
            RoutineSearchFilter defaultFilter = RoutineSearchFilter.defaultFilter();
            Sort sort = Sort.by(Sort.Direction.fromString(defaultFilter.getSortDir()), defaultFilter.getSortBy());
            return routineRepository.findAll(sort).stream()
                    .map(routine -> {
                        List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
                        List<DrillDTO> dtoList = CollectionUtils.isEmpty(drills) ? Collections.emptyList() :
                                drills.stream().map(FitmateServiceUtil.DrillUtil::buildDTO).toList();
                        return FitmateServiceUtil.RoutineUtil.buildDto(routine, dtoList);
                    }).toList();
        }
        spec = StringUtils.isNotEmpty(filter.getUsername()) ? spec.and((root, query, cb) -> cb.equal(root.get("username"), filter.getUsername())) : spec;
        spec = StringUtils.isNotEmpty(filter.getTrainingRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("training").get("refId"), Long.valueOf(filter.getUsername()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and((root, query, cb) -> cb.equal(root.get("refId"), filter.getRefId())) : spec;
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid())) : spec;
        spec = StringUtils.isNotEmpty(filter.getStatus()) ? spec.and((root, query, cb) -> cb.equal(root.get("status"), Status.RoutineStatus.toInt(filter.getStatus()))) : spec;
        spec = filter.getRoutineDateFrom() != null ? spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("routineDate"), filter.getRoutineDateFrom())) : spec;
        spec = filter.getRoutineDateTo() != null ? spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("routineDate"), filter.getRoutineDateTo())) : spec;
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        return routineRepository.findAll(spec, sort).stream()
                .map(routine -> {
                    List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
                    List<DrillDTO> dtoList = CollectionUtils.isEmpty(drills) ? Collections.emptyList() :
                            drills.stream().map(FitmateServiceUtil.DrillUtil::buildDTO).toList();
                    return FitmateServiceUtil.RoutineUtil.buildDto(routine, dtoList);
                }).toList();
    }

    @Override
    public List<RoutineDrillLog> findRoutineDrillLog(String username) {
        List<RoutineDrillLog> drillLogs = new LinkedList<>();
        routineRepository.findRoutineAndMergeColumnsByUsername(username).forEach(row -> {
            String trainingName = asString(row[0]);
            LocalDate routineDate = asLocalDate(row[1]);
            String exerciseName = asString(row[2]);
            String unit = asString(row[3]);
            double measurement = asDouble(row[4]);
            String measurementUnit = asString(row[5]);
            int repetition = asInt(row[6]);
            String bodyPartName = asString(row[7]);
            drillLogs.add(RoutineDrillLog.builder()
                    .training(trainingName)
                    .routineDate(routineDate)
                    .exerciseName(exerciseName)
                    .unit(unit)
                    .measurement(measurement)
                    .measurementUnit(measurementUnit)
                    .repetition(repetition)
                    .bodyPartName(bodyPartName)
                    .build());
        });
        return drillLogs;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private double asDouble(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private int asInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private LocalDate asLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        if (value instanceof java.util.Date date) {
            return new Date(date.getTime()).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }
}
