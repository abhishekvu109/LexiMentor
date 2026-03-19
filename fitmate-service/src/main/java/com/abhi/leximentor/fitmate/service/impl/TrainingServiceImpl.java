package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.TrainingDTO;
import com.abhi.leximentor.fitmate.entities.Training;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.TrainingRepository;
import com.abhi.leximentor.fitmate.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public List<TrainingDTO> addAll(List<TrainingDTO> trainingDTOS) {
        List<Training> entities = trainingRepository.saveAll(trainingDTOS.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildEntity).toList());
        return entities.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }

    @Override
    public TrainingDTO getByRefId(long refId) {
        Training training = trainingRepository.findByRefId(refId);
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(training);
    }

    @Override
    public List<TrainingDTO> getAllByRefId(List<Long> refIds) {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Training> entities = trainingRepository.findByRefIdIn(refIds);
        if (entities.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return entities.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }

    @Override
    public TrainingDTO getByName(String name) {
        Training training = trainingRepository.findByName(name);
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(training);
    }

    @Override
    @Transactional
    public TrainingDTO update(TrainingDTO trainingDTO) {
        Training training = trainingRepository.findByRefId(Long.parseLong(trainingDTO.getRefId()));
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        training.setName(trainingDTO.getName());
        training.setDescription(training.getDescription());
        training.setStatus(Status.ApplicationStatus.getStatus(trainingDTO.getStatus()));
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(trainingRepository.save(training));
    }

    @Override
    @Transactional
    public void delete(TrainingDTO trainingDTO) {
        Training training = trainingRepository.findByRefId(Long.parseLong(trainingDTO.getRefId()));
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        trainingRepository.delete(training);
    }

    @Override
    @Transactional
    public void deleteAll(List<TrainingDTO> trainingDTOS) {
        List<Long> refIds = trainingDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList();
        List<Training> entities = trainingRepository.findByRefIdIn(refIds);
        trainingRepository.deleteAll(entities);
    }

    @Override
    public List<TrainingDTO> getAll() {
        List<Training> trainingList = trainingRepository.findAll();
        return trainingList.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }
}
