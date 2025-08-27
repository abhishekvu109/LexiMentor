package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.service.BodyPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BodyPartsServiceImpl implements BodyPartService {

    private final BodyPartsRepository bodyPartsRepository;

    @Override
    @Transactional
    public List<BodyPartsDTO> addAll(List<BodyPartsDTO> bodyPartsDTOS) {
        log.info("Received a request to add all the body parts");
        List<BodyPart> entities = bodyPartsDTOS.stream().map(FitmateServiceUtil.BodyPartsUtil::buildEntity).toList();
        log.info("The util has converted the entities: {}", entities.size());
        entities = bodyPartsRepository.saveAll(entities);
        log.info("The entities have been saved : {}", entities);
        return entities.stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public List<BodyPartsDTO> getAll() {
        List<BodyPart> bodyParts = bodyPartsRepository.findAll();
        return bodyParts.stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public BodyPartsDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        BodyPart bodyPart = bodyPartsRepository.findByRefId(refId);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPart);
    }

    @Override
    public List<BodyPartsDTO> getAllByRefId(List<Long> refIds) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<BodyPart> bodyParts = bodyPartsRepository.findByRefIdIn(refIds);
        if (refIds.size() != bodyParts.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return bodyPartsRepository.findByRefIdIn(refIds).stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public BodyPartsDTO getByName(String name) throws ServerException.EntityObjectNotFound {
        BodyPart bodyPart = bodyPartsRepository.findByName(name);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPart);
    }

    @Override
    public BodyPartsDTO update(BodyPartsDTO bodyPartsDTO) throws ServerException.EntityObjectNotFound {
        BodyPart bodyPart = bodyPartsRepository.findByRefId(Long.parseLong(bodyPartsDTO.getRefId()));
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyPart.setName(bodyPart.getName());
        bodyPart.setStatus(Status.ApplicationStatus.getStatus(bodyPartsDTO.getStatus()));
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPartsRepository.save(bodyPart));
    }

    @Override
    public void delete(BodyPartsDTO bodyPartsDTO) {
        BodyPart bodyPart = bodyPartsRepository.findByRefId(Long.parseLong(bodyPartsDTO.getRefId()));
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyPartsRepository.delete(bodyPart);
    }

    @Override
    public void deleteAll(List<BodyPartsDTO> bodyPartsDTOS) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(bodyPartsDTOS))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<BodyPart> bodyParts = bodyPartsRepository.findByRefIdIn(bodyPartsDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList());
        if (bodyPartsDTOS.size() != bodyParts.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        bodyPartsRepository.deleteAll(bodyParts);
    }
}
