package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.DrillRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.service.DrillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<DrillDTO> findAll() {
        return drillRepository.findAll()
                .stream()
                .map(FitmateServiceUtil.DrillUtil::buildDTO)
                .toList();
    }

    @Override
    public List<DrillDTO> findByExerciseOrderByCrtnDate(Exercise exercise) {
        return drillRepository.findByExerciseOrderByCrtnDateDesc(exercise)
                .stream()
                .map(FitmateServiceUtil.DrillUtil::buildDTO)
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
    @Transactional
    public DrillDTO update(DrillDTO drillDTO) {
        Drill drill = drillRepository.findByRefId(Long.parseLong(drillDTO.getRefId()));
        if (drill == null) {
            throw new ServerException().new EntityObjectNotFound("Drill object is not found.");
        }
        drill.setUnit(drillDTO.getUnit());
        drill.setMeasurement(drillDTO.getMeasurement());
        drill.setRepetition(drillDTO.getRepetition());
        drill.setBurntCalories(drillDTO.getBurntCalories());
        return FitmateServiceUtil.DrillUtil.buildDTO(drillRepository.save(drill));
    }

    @Override
    @Transactional
    public void delete(DrillDTO drillDTO) {
        Drill drill = drillRepository.findByRefId(Long.parseLong(drillDTO.getRefId()));
        if (drill == null) {
            throw new ServerException().new EntityObjectNotFound("Drill object is not found.");
        }
        drillRepository.delete(drill);
    }
}
