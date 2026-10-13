package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Muscle;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.MuscleMapper;
import com.abhi.leximentor.fitmate.repository.BodyPartsRepository;
import com.abhi.leximentor.fitmate.repository.MuscleRepository;
import com.abhi.leximentor.fitmate.service.MuscleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MuscleServiceImpl implements MuscleService {

    private final MuscleRepository muscleRepository;
    private final BodyPartsRepository bodyPartsRepository;
    private final MuscleMapper muscleMapper;

    @Override
    @Transactional
    public List<MuscleDTO> addAll(List<MuscleDTO> request) {
        List<BodyPart> bodyParts = bodyPartsRepository.findAll();
        List<Muscle> muscles = request.stream()
                .map(dto -> {
                    BodyPart bodyPart = bodyParts.stream()
                            .filter(bp -> StringUtils.equalsIgnoreCase(bp.getName(), dto.getBodyPart().getName()))
                            .findAny()
                            .orElseThrow(() -> new ServerException().new InternalError("Body part not found"));
                    return muscleMapper.toEntity(dto, bodyPart);
                })
                .toList();
        muscles = muscleRepository.saveAll(muscles);
        return muscles.stream()
                .map(muscle -> {
                    BodyPart bodyPart = bodyParts.stream()
                            .filter(bp -> bp.getRefId() == muscle.getBodyPart())
                            .findAny()
                            .orElseThrow(() -> new ServerException().new InternalError("Unable to find the equivalent Body Part."));
                    return muscleMapper.toDto(muscle, bodyPart);
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteAll() {
        muscleRepository.deleteAll();
    }

    @Override
    public MuscleDTO findByRefId(long refId) {
        Muscle muscle = muscleRepository.findByRefId(refId);
        return muscleMapper.toDto(muscle, bodyPartsRepository.findByRefId(muscle.getBodyPart()));
    }

    @Override
    public MuscleDTO findByName(String name) {
        Muscle muscle = muscleRepository.findByName(name);
        return muscleMapper.toDto(muscle, bodyPartsRepository.findByRefId(muscle.getBodyPart()));
    }

    @Override
    @Transactional
    public void deleteByRefId(long refId) {
        muscleRepository.delete(muscleRepository.findByRefId(refId));
    }

    @Override
    public List<MuscleDTO> findAll() {
        List<BodyPart> bodyParts = bodyPartsRepository.findAll();
        return muscleRepository.findAll()
                .stream()
                .map(muscle -> {
                    BodyPart bodyPart = bodyParts.stream()
                            .filter(bp -> bp.getRefId() == muscle.getBodyPart())
                            .findAny()
                            .orElseThrow(() -> new ServerException().new InternalError("Body Part is not found"));
                    return muscleMapper.toDto(muscle, bodyPart);
                })
                .toList();
    }

    @Override
    public PagedResponse<MuscleDTO> findAll(Pageable pageable) {
        Page<Muscle> musclePage = muscleRepository.findAll(pageable);
        // Fetch only the body parts referenced by this page (avoids loading all body parts)
        Set<Long> bodyPartRefIds = musclePage.getContent().stream()
                .map(Muscle::getBodyPart)
                .collect(Collectors.toSet());
        Map<Long, BodyPart> bodyPartMap = bodyPartsRepository
                .findByRefIdIn(new ArrayList<>(bodyPartRefIds))
                .stream()
                .collect(Collectors.toMap(BodyPart::getRefId, bp -> bp));
        return PagedResponse.of(musclePage, muscle -> {
            BodyPart bodyPart = bodyPartMap.get(muscle.getBodyPart());
            if (bodyPart == null)
                throw new ServerException().new InternalError("Body Part not found for muscle: " + muscle.getName());
            return muscleMapper.toDto(muscle, bodyPart);
        });
    }
}
