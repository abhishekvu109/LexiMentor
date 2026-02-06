package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.filters.ExerciseSearchFilter;
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
    List<ExerciseDTO> getAllWithAnalytics();

    List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadatRefId);

    List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId, long targetBodyPartRefId);

    ExerciseDTO getByName(String name);

    ExerciseDTO update(ExerciseDTO excerciseDTO);

    void delete(ExerciseDTO exerciseDTO);

    void deleteAll(List<ExerciseDTO> exerciseDTOS);

    void deleteAll();

    void updateResource(ExerciseDTO exerciseDTO, List<MultipartFile> files);

    void updateResource(ExerciseDTO exerciseDTO, List<MultipartFile> files, String placeholder);

    List<Map<String, Object>> findAllResources(long refId);

    Optional<GridFsResource> findResource(long refId, String resourceId);

    Optional<GridFsResource> findResource(long refId, String resourceId, String placeholder);

    List<ExerciseDTO> search(ExerciseSearchFilter filter);
}
