package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.service.BodyPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BodyPartsServiceImpl implements BodyPartService {

    private final BodyPartsRepository bodyPartsRepository;

    @Override
    public List<BodyPartsDTO> addAll(List<BodyPartsDTO> bodyPartsDTOS) {
        List<BodyParts> entities = bodyPartsDTOS.stream().map(FitmateServiceUtil.BodyPartsUtil::buildEntity).toList();
        entities = bodyPartsRepository.saveAll(entities);
        return entities.stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public BodyPartsDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        BodyParts bodyParts = bodyPartsRepository.findByRefId(refId);
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyParts);
    }

    @Override
    public List<BodyPartsDTO> getAllByRefId(List<Long> refIds) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<BodyParts> bodyParts = bodyPartsRepository.findByRefIdIn(refIds);
        if (refIds.size() != bodyParts.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return bodyPartsRepository.findByRefIdIn(refIds).stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public BodyPartsDTO getByName(String name) throws ServerException.EntityObjectNotFound {
        BodyParts bodyParts = bodyPartsRepository.findByName(name);
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyParts);
    }

    @Override
    public BodyPartsDTO update(BodyPartsDTO bodyPartsDTO) throws ServerException.EntityObjectNotFound {
        BodyParts bodyParts = bodyPartsRepository.findByRefId(Long.parseLong(bodyPartsDTO.getRefId()));
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyParts.setName(bodyParts.getName());
        bodyParts.setStatus(Status.ApplicationStatus.getStatus(bodyPartsDTO.getStatus()));
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPartsRepository.save(bodyParts));
    }

    @Override
    public void delete(BodyPartsDTO bodyPartsDTO) {
        BodyParts bodyParts = bodyPartsRepository.findByRefId(Long.parseLong(bodyPartsDTO.getRefId()));
        if (bodyParts == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyPartsRepository.delete(bodyParts);
    }

    @Override
    public void deleteAll(List<BodyPartsDTO> bodyPartsDTOS) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(bodyPartsDTOS))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<BodyParts> bodyParts = bodyPartsRepository.findByRefIdIn(bodyPartsDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList());
        if (bodyPartsDTOS.size() != bodyParts.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyPartsRepository.deleteAll(bodyParts);
    }
}
