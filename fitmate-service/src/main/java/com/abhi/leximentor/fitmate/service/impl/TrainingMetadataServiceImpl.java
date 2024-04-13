package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.TrainingMetadataDTO;
import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.TrainingMetadataRepository;
import com.abhi.leximentor.fitmate.service.TrainingMetaDataService;
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
public class TrainingMetadataServiceImpl implements TrainingMetaDataService {
    private final TrainingMetadataRepository trainingMetadataRepository;

    @Override
    @Transactional
    public List<TrainingMetadataDTO> addAll(List<TrainingMetadataDTO> trainingMetadataDTOS) {
        List<TrainingMetadata> entities = trainingMetadataRepository.saveAll(trainingMetadataDTOS.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildEntity).toList());
        return entities.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }

    @Override
    public TrainingMetadataDTO getByRefId(long refId) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(refId);
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(trainingMetadata);
    }

    @Override
    public List<TrainingMetadataDTO> getAllByRefId(List<Long> refIds) {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<TrainingMetadata> entities = trainingMetadataRepository.findByRefIdIn(refIds);
        if (entities.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return entities.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }

    @Override
    public TrainingMetadataDTO getByName(String name) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByName(name);
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(trainingMetadata);
    }

    @Override
    @Transactional
    public TrainingMetadataDTO update(TrainingMetadataDTO trainingMetadataDTO) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(Long.parseLong(trainingMetadataDTO.getRefId()));
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        trainingMetadata.setName(trainingMetadataDTO.getName());
        trainingMetadata.setDescription(trainingMetadata.getDescription());
        trainingMetadata.setStatus(Status.ApplicationStatus.getStatus(trainingMetadataDTO.getStatus()));
        return FitmateServiceUtil.TrainingMetadataUtil.buildDto(trainingMetadataRepository.save(trainingMetadata));
    }

    @Override
    @Transactional
    public void delete(TrainingMetadataDTO trainingMetadataDTO) {
        TrainingMetadata trainingMetadata = trainingMetadataRepository.findByRefId(Long.parseLong(trainingMetadataDTO.getRefId()));
        if (trainingMetadata == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        trainingMetadataRepository.delete(trainingMetadata);
    }

    @Override
    @Transactional
    public void deleteAll(List<TrainingMetadataDTO> trainingMetadataDTOS) {
        List<Long> refIds = trainingMetadataDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList();
        List<TrainingMetadata> entities = trainingMetadataRepository.findByRefIdIn(refIds);
        trainingMetadataRepository.deleteAll(entities);
    }

    @Override
    public List<TrainingMetadataDTO> getAll() {
        List<TrainingMetadata> trainingMetadataList = trainingMetadataRepository.findAll();
        return trainingMetadataList.stream().map(FitmateServiceUtil.TrainingMetadataUtil::buildDto).toList();
    }
}
