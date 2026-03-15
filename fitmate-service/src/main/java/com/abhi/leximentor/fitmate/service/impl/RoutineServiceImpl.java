package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDrillLog;
import com.abhi.leximentor.fitmate.dto.filters.RoutineSearchFilter;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.entities.Training;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.DrillMapper;
import com.abhi.leximentor.fitmate.mapper.RoutineMapper;
import com.abhi.leximentor.fitmate.repository.*;
import com.abhi.leximentor.fitmate.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final RoutineMapper routineMapper;
    private final DrillMapper drillMapper;

    @Override
    @Transactional
    public RoutineDTO add(RoutineDTO routineDTO) {
        Training training = trainingRepository.findByRefId(Long.parseLong(routineDTO.getTraining().getRefId()));
        Routine routine = routineMapper.toEntity(routineDTO, training);
        List<Drill> drills = new LinkedList<>();
        routineDTO.getDrills().forEach(dto -> {
            Drill drill = drillMapper.toEntity(dto);
            Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getExercise().getRefId()));
            if (exercise == null) throw new ServerException().new InternalError("The exercise object is not found.");
            drill.setExercise(exercise);
            drill.setRoutine(routine.getRefId());
            drill.setRoutineObj(routine);
            drills.add(drill);
        });
        routine.setDrills(drills);
        return routineMapper.toDto(routineRepository.save(routine));
    }

    @Override
    @Transactional
    public RoutineDTO addByNames(RoutineDTO routineDTO) {
        Training training = trainingRepository.findByNameIgnoreCase(routineDTO.getTraining().getName());
        Routine routine = routineMapper.toEntity(routineDTO, training);
        List<Drill> drills = new LinkedList<>();
        routineDTO.getDrills().forEach(dto -> {
            Drill drill = drillMapper.toEntity(dto);
            Exercise exercise = exerciseRepository.findByName(dto.getExercise().getName());
            if (exercise == null) throw new ServerException().new InternalError("The exercise object is not found.");
            drill.setExercise(exercise);
            drill.setRoutine(routine.getRefId());
            drill.setRoutineObj(routine);
            drills.add(drill);
        });
        routine.setDrills(drills);
        return routineMapper.toDto(routineRepository.save(routine));
    }

    @Override
    public RoutineDTO getByRefId(long refId) {
        Routine routine = routineRepository.findByRefId(refId);
        if (routine == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
        routine.setDrills(drills);
        return routineMapper.toDto(routine);
    }

    @Override
    public List<RoutineDTO> getAllByRefId(List<Long> refIds) throws ServerException.InternalError, ServerException.EntityObjectNotFound {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Routine> entities = routineRepository.findByRefIdIn(refIds);
        if (entities.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return entities.stream().map(routineMapper::toDto).toList();
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
        if (routineDTO.getDrills() != null) {
            List<Drill> existing = drillRepository.findByRoutine(routine.getRefId());
            List<Drill> drills = new LinkedList<>();
            routineDTO.getDrills().forEach(dto -> {
                Drill drill = StringUtils.isNotEmpty(dto.getRefId())
                        ? drillRepository.findByRefId(Long.parseLong(dto.getRefId()))
                        : drillMapper.toEntity(dto);
                Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getExercise().getRefId()));
                if (exercise == null) throw new ServerException().new InternalError("The exercise object is not found.");
                drill.setExercise(exercise);
                drill.setRoutine(routine.getRefId());
                drill.setRoutineObj(routine);
                drills.add(drill);
            });
            List<Long> incomingRefIds = routineDTO.getDrills().stream()
                    .filter(dto -> StringUtils.isNotBlank(dto.getRefId()))
                    .map(dto -> Long.parseLong(dto.getRefId()))
                    .toList();
            List<Drill> toRemove = existing.stream()
                    .filter(drill -> !incomingRefIds.contains(drill.getRefId()))
                    .toList();
            if (!toRemove.isEmpty()) drillRepository.deleteAll(toRemove);
            routine.setDrills(drills);
        }
        return routineMapper.toDto(routineRepository.save(routine));
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
        List<Routine> routines = routineRepository.findByRefIdIn(
                routineDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList());
        routineRepository.deleteAll(routines);
    }

    @Override
    public List<RoutineDTO> search(RoutineSearchFilter filter) {
        RoutineSearchFilter effectiveFilter = (filter == null || filter.isEmpty())
                ? RoutineSearchFilter.defaultFilter()
                : filter;
        Sort sort = Sort.by(Sort.Direction.fromString(effectiveFilter.getSortDir()), effectiveFilter.getSortBy());
        Specification<Routine> spec = buildRoutineSpec(effectiveFilter);
        return routineRepository.findAll(spec, sort).stream()
                .map(routine -> {
                    List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
                    List<DrillDTO> dtoList = CollectionUtils.isEmpty(drills)
                            ? Collections.emptyList()
                            : drills.stream().map(drillMapper::toDto).toList();
                    return routineMapper.toDto(routine, dtoList);
                }).toList();
    }

    @Override
    public PagedResponse<RoutineDTO> search(RoutineSearchFilter filter, Pageable pageable) {
        RoutineSearchFilter effectiveFilter = (filter == null || filter.isEmpty())
                ? RoutineSearchFilter.defaultFilter()
                : filter;
        Sort sort = Sort.by(Sort.Direction.fromString(effectiveFilter.getSortDir()), effectiveFilter.getSortBy());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Specification<Routine> spec = buildRoutineSpec(effectiveFilter);
        return PagedResponse.of(routineRepository.findAll(spec, sortedPageable), routine -> {
            List<Drill> drills = drillRepository.findByRoutine(routine.getRefId());
            List<DrillDTO> dtoList = CollectionUtils.isEmpty(drills)
                    ? Collections.emptyList()
                    : drills.stream().map(drillMapper::toDto).toList();
            return routineMapper.toDto(routine, dtoList);
        });
    }

    private Specification<Routine> buildRoutineSpec(RoutineSearchFilter filter) {
        Specification<Routine> spec = Specification.where(null);
        if (filter == null) return spec;
        spec = StringUtils.isNotEmpty(filter.getUsername()) ? spec.and((root, query, cb) -> cb.equal(root.get("username"), filter.getUsername())) : spec;
        spec = StringUtils.isNotEmpty(filter.getTrainingRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("training").get("refId"), Long.valueOf(filter.getTrainingRefId()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and((root, query, cb) -> cb.equal(root.get("refId"), filter.getRefId())) : spec;
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid())) : spec;
        spec = StringUtils.isNotEmpty(filter.getStatus()) ? spec.and((root, query, cb) -> cb.equal(root.get("status"), Status.RoutineStatus.toInt(filter.getStatus()))) : spec;
        spec = filter.getRoutineDateFrom() != null ? spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("routineDate"), filter.getRoutineDateFrom())) : spec;
        spec = filter.getRoutineDateTo() != null ? spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("routineDate"), filter.getRoutineDateTo())) : spec;
        return spec;
    }

    @Override
    public List<RoutineDrillLog> findRoutineDrillLog(String username) {
        List<RoutineDrillLog> drillLogs = new LinkedList<>();
        routineRepository.findRoutineAndMergeColumnsByUsername(username).forEach(row -> {
            drillLogs.add(RoutineDrillLog.builder()
                    .training(asString(row[0]))
                    .routineDate(asLocalDate(row[1]))
                    .exerciseName(asString(row[2]))
                    .unit(asString(row[3]))
                    .measurement(asDouble(row[4]))
                    .measurementUnit(asString(row[5]))
                    .repetition(asInt(row[6]))
                    .bodyPartName(asString(row[7]))
                    .build());
        });
        return drillLogs;
    }

    // -----------------------------------------------------------------------
    // Private helpers for raw-query result casting
    // -----------------------------------------------------------------------

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private double asDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number number) return number.doubleValue();
        return Double.parseDouble(value.toString());
    }

    private int asInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

    private LocalDate asLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate localDate) return localDate;
        if (value instanceof Date date) return date.toLocalDate();
        if (value instanceof java.util.Date date) return new Date(date.getTime()).toLocalDate();
        return LocalDate.parse(value.toString());
    }
}
