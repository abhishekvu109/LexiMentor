package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.service.BodyPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public BodyPartsDTO getByRefId(long refId) {
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPartsRepository.findByRefId(refId));
    }

    @Override
    public List<BodyPartsDTO> getAllByRefId(List<Long> refIds) {
        return bodyPartsRepository.findByRefIdIn(refIds).stream().map(FitmateServiceUtil.BodyPartsUtil::buildDto).toList();
    }

    @Override
    public BodyPartsDTO getByName(String name) {
        return FitmateServiceUtil.BodyPartsUtil.buildDto(bodyPartsRepository.findByName(name));
    }

    @Override
    public void update(BodyPartsDTO bodyPartsDTO) {
        BodyParts bodyParts = bodyPartsRepository.findByRefId(Long.parseLong(bodyPartsDTO.getRefId()));
        bodyParts.setName(bodyParts.getName());
        bodyParts.setStatus(Status.ApplicationStatus.getStatus(bodyPartsDTO.getStatus()));
        bodyPartsRepository.save(bodyParts);
    }

    @Override
    public void delete(BodyPartsDTO bodyPartsDTO) {

    }

    @Override
    public void deleteAll(List<BodyPartsDTO> bodyPartsDTOS) {

    }
}
