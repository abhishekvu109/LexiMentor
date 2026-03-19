package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Muscle;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.repository.MuscleRepository;
import com.abhi.leximentor.fitmate.service.MuscleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MuscleServiceImpl implements MuscleService {

    private final MuscleRepository muscleRepository;
    private final BodyPartsRepository bodyPartsRepository;

    @Override
    @Transactional
    public List<MuscleDTO> addAll(List<MuscleDTO> request) {
        List<BodyPart> bodyParts = bodyPartsRepository.findAll();
        List<Muscle> muscles = request.stream().map(muscle -> FitmateServiceUtil.MuscleUtil.buildEntity(muscle, bodyParts.stream().filter(bodyPart -> StringUtils.equalsIgnoreCase(bodyPart.getName(), muscle.getBodyPart().getName())).findAny().orElseThrow(() -> new ServerException().new InternalError("Body part not found")))).toList();
        muscles = muscleRepository.saveAll(muscles);
        return muscles.stream().map(muscle -> FitmateServiceUtil.MuscleUtil.buildDTO(muscle, bodyParts.stream().filter(bp -> bp.getRefId() == muscle.getBodyPart()).findAny().orElseThrow(() -> new ServerException().new InternalError("Unable to find the equivalent Body Part.")))).toList();
    }

    @Override
    @Transactional
    public void deleteAll() {
        muscleRepository.deleteAll();
    }

    @Override
    public MuscleDTO findByRefId(long refId) {
        Muscle muscle = muscleRepository.findByRefId(refId);
        return FitmateServiceUtil.MuscleUtil.buildDTO(muscle, bodyPartsRepository.findByRefId(muscle.getBodyPart()));
    }

    @Override
    public MuscleDTO findByName(String name) {
        Muscle muscle = muscleRepository.findByName(name);
        return FitmateServiceUtil.MuscleUtil.buildDTO(muscle, bodyPartsRepository.findByRefId(muscle.getBodyPart()));
    }

    @Override
    @Transactional
    public void deleteByRefId(long refId) {
        muscleRepository.delete(muscleRepository.findByRefId(refId));
    }

    @Override
    public List<MuscleDTO> findAll() {
        List<BodyPart> bodyParts = bodyPartsRepository.findAll();
        return muscleRepository
                .findAll()
                .stream()
                .map(muscle ->
                        FitmateServiceUtil
                                .MuscleUtil
                                .buildDTO(muscle,
                                        bodyParts
                                                .stream()
                                                .filter(bodyPart -> bodyPart.getRefId() == muscle.getBodyPart())
                                                .findAny()
                                                .orElseThrow(() -> new ServerException().new InternalError("Body Part is not found"))))
                .toList();
    }
}
