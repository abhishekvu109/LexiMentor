package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.constants.ResourceExtension;
import com.abhi.leximentor.fitmate.constants.ResourceType;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExerciseService {


    List<ExerciseDTO> addAll(List<ExerciseDTO> excerciseDTOS);

    ExerciseDTO getByRefId(long refId);

    List<ExerciseDTO> getByBodyPartRefId(long bodyPartRefId);

    List<ExerciseDTO> getAllByRefId(List<Long> refIds);

    List<ExerciseDTO> getAll();

    List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadatRefId);

    List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId, long targetBodyPartRefId);

    ExerciseDTO getByName(String name);

    ExerciseDTO update(ExerciseDTO excerciseDTO);

    void delete(ExerciseDTO exerciseDTO);

    void deleteAll(List<ExerciseDTO> exerciseDTOS);

    void deleteAll();

    void updateResource(ExerciseDTO exerciseDTO,List<MultipartFile> files);

    List<Map<String, Object>> findAllResources(long refId);

    Optional<GridFsResource> findResource(long refId,String resourceId);
}
