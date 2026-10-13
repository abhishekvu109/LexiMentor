package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.dto.TrainingDTO;
import com.abhi.leximentor.fitmate.entities.Training;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.TrainingMapper;
import com.abhi.leximentor.fitmate.repository.TrainingRepository;
import com.abhi.leximentor.fitmate.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;

    @Override
    @Transactional
    public List<TrainingDTO> addAll(List<TrainingDTO> trainingDTOS) {
        List<Training> entities = trainingRepository.saveAll(
                trainingDTOS.stream().map(trainingMapper::toEntity).toList());
        return entities.stream().map(trainingMapper::toDto).toList();
    }

    @Override
    public TrainingDTO getByRefId(long refId) {
        Training training = trainingRepository.findByRefId(refId);
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return trainingMapper.toDto(training);
    }

    @Override
    public List<TrainingDTO> getAllByRefId(List<Long> refIds) {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Training> entities = trainingRepository.findByRefIdIn(refIds);
        if (entities.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return entities.stream().map(trainingMapper::toDto).toList();
    }

    @Override
    public TrainingDTO getByName(String name) {
        Training training = trainingRepository.findByName(name);
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return trainingMapper.toDto(training);
    }

    @Override
    @Transactional
    public TrainingDTO update(TrainingDTO trainingDTO) {
        Training training = trainingRepository.findByRefId(Long.parseLong(trainingDTO.getRefId()));
        if (training == null)
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        training.setName(trainingDTO.getName());
        training.setDescription(trainingDTO.getDescription());
        training.setStatus(Status.ApplicationStatus.getStatus(trainingDTO.getStatus()));
        return trainingMapper.toDto(trainingRepository.save(training));
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
        return trainingRepository.findAll().stream().map(trainingMapper::toDto).toList();
    }

    @Override
    public PagedResponse<TrainingDTO> getAll(Pageable pageable) {
        return PagedResponse.of(trainingRepository.findAll(pageable), trainingMapper::toDto);
    }
}
