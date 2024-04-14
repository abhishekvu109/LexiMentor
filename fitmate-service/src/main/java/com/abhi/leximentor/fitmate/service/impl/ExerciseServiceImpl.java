package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.repository.ExerciseRepository;
import com.abhi.leximentor.fitmate.repository.TrainingMetadataRepository;
import com.abhi.leximentor.fitmate.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final TrainingMetadataRepository trainingMetadataRepository;
    private final BodyPartsRepository bodyPartsRepository;

    @Override
    @Transactional
    public List<ExerciseDTO> addAll(List<ExerciseDTO> exerciseDTOS) throws ServerException.EntityObjectNotFound {
        List<Exercise> exercises = new LinkedList<>();
        for (ExerciseDTO exerciseDTO : exerciseDTOS) {
            TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(Long.parseLong(exerciseDTO.getTrainingMetadata().getRefId()));
            if (trainingMetadata == null)
                throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
            BodyParts bodyParts = bodyPartsRepository.findByRefId(Long.parseLong(exerciseDTO.getTargetBodyPart().getRefId()));
            if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
            List<BodyParts> bodyPartsList = (CollectionUtils.isNotEmpty(exerciseDTO.getSecondaryBodyParts())) ? bodyPartsRepository.findByNameIn(exerciseDTO.getSecondaryBodyParts()) : null;
            exercises.add(FitmateServiceUtil.ExcerciseUtil.buildEntity(exerciseDTO, trainingMetadata, bodyParts, bodyPartsList));
        }
        List<Exercise> response = exerciseRepository.saveAll(exercises);
        return response.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }

    @Override
    public ExerciseDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(refId);
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.ExcerciseUtil.buildDto(exercise);
    }

    @Override
    public List<ExerciseDTO> getAllByRefId(List<Long> refIds) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(refIds);
        if (exercises.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.GENERIC_EXCEPTION);
        return exercises.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }

    @Override
    public ExerciseDTO getByName(String name) {
        Exercise exercise = exerciseRepository.findByName(name.toUpperCase());
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.ExcerciseUtil.buildDto(exercise);
    }

    @Override
    @Transactional
    public ExerciseDTO update(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setUnit(exerciseDTO.getUnit());
        exercise.setStatus(Status.ApplicationStatus.getStatus(exerciseDTO.getStatus()));
        BodyParts targetBodyPart = bodyPartsRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (targetBodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setTargetBodyPart(targetBodyPart);
        return FitmateServiceUtil.ExcerciseUtil.buildDto(exerciseRepository.save(exercise));
    }

    @Override
    public List<ExerciseDTO> getByBodyPartRefId(long bodyPartRefId) throws ServerException.EntityObjectNotFound {
        BodyParts bodyParts = bodyPartsRepository.findByRefId(bodyPartRefId);
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTargetBodyPart(bodyParts);
        return exercises.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }

    @Override
    @Transactional
    public void delete(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exerciseRepository.delete(exercise);
    }

    @Override
    @Transactional
    public void deleteAll(List<ExerciseDTO> exerciseDTOS) {
        List<Long> exerciseRefIds = exerciseDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList();
        log.info("Converted all the list of exercise IDs: {}", exerciseRefIds);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(exerciseRefIds);
        log.info("Fetched all the exercise data from the database: {}", exercises);
        exerciseRepository.deleteAll(exercises);
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadataRefId) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(trainingMetadataRefId);
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTrainingMetadata(trainingMetadata);
        return exercises.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId, long targetBodyPartRefId) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(trainingMetadatRefId);
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        BodyParts bodyParts = bodyPartsRepository.findByRefId(targetBodyPartRefId);
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTrainingMetadataAndTargetBodyPart(trainingMetadata, bodyParts);
        return exercises.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAll() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream().map(FitmateServiceUtil.ExcerciseUtil::buildDto).toList();
    }
}
