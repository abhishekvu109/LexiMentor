package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.constants.Unit;
import com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.filters.ExerciseSearchFilter;
import com.abhi.leximentor.fitmate.entities.*;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.repository.*;
import com.abhi.leximentor.fitmate.service.ExerciseService;
import com.abhi.leximentor.fitmate.service.ResourceService;
import com.abhi.leximentor.fitmate.util.KeyGeneratorUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;
    private final BodyPartsRepository bodyPartsRepository;
    private final MuscleRepository muscleRepository;
    private final ResourceService resourceService;
    private final FitmateResourceRepository fitmateResourceRepository;
    private final DrillRepository drillRepository;

    @Override
    @Transactional
    public List<ExerciseDTO> addAll(List<ExerciseDTO> exerciseDTOS) throws ServerException.EntityObjectNotFound {
        List<Exercise> exercises = new LinkedList<>();
        for (ExerciseDTO exerciseDTO : exerciseDTOS) {
            Training training = StringUtils.isNotEmpty(exerciseDTO.getTraining().getName()) ? trainingRepository.findByNameIgnoreCase(exerciseDTO.getTraining().getName()) : trainingRepository.findByRefId(Long.parseLong(exerciseDTO.getTraining().getRefId()));
            if (training == null) {
                log.error("The training object is not found.");
                throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND + " {Training Object.} ");
            }
            BodyPart bodyPart = StringUtils.isNotEmpty(exerciseDTO.getBodyPart().getName()) ?
                    bodyPartsRepository.findByName(exerciseDTO.getBodyPart().getName()) :
                    bodyPartsRepository.findByRefId(Long.parseLong(exerciseDTO.getBodyPart().getRefId()));
            if (bodyPart == null) {
                log.error("The body part object is not found.");
                throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND + "{Body part object}");
            }
            List<Muscle> muscles = null;
            if (CollectionUtils.isNotEmpty(exerciseDTO.getTargetMuscles())) {
                muscles = muscleRepository.findByNameInIgnoreCase(exerciseDTO
                        .getTargetMuscles()
                        .stream()
                        .filter(muscle -> StringUtils.isNotEmpty(muscle.getName()))
                        .map(muscle -> muscle.getName().toLowerCase(Locale.ROOT))
                        .toList());
            }
            exercises.add(FitmateServiceUtil.ExerciseUtil.buildEntity(exerciseDTO, training, bodyPart, muscles));
        }

        List<Exercise> response = exerciseRepository.saveAll(exercises);
//        return response.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
        List<ExerciseDTO> output = new LinkedList<>();
        response.forEach(exercise -> {
            try {
                output.add(FitmateServiceUtil.ExerciseUtil.buildDto(exercise));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return output;
    }

    @Override
    public ExerciseDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(refId);
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.ExerciseUtil.buildDto(exercise);
    }

    @Override
    public List<ExerciseDTO> getAllByRefId(List<Long> refIds) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(refIds);
        if (exercises.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.GENERIC_EXCEPTION);
        return exercises.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    public ExerciseDTO getByName(String name) {
        Exercise exercise = exerciseRepository.findByName(name.toUpperCase());
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return FitmateServiceUtil.ExerciseUtil.buildDto(exercise);
    }

    @Override
    @Transactional
    public ExerciseDTO update(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setUnit(Unit.from(exercise.getUnit()).getValue());
        exercise.setStatus(Status.ApplicationStatus.getStatus(exerciseDTO.getStatus()));
        BodyPart targetBodyPart = bodyPartsRepository.findByNameIgnoreCase(exerciseDTO.getBodyPart().getName());
        if (targetBodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setTargetBodyPart(targetBodyPart);
        exercise.setEquipments(null);
        exercise.setEquipments(exerciseDTO.getEquipments());
        return FitmateServiceUtil.ExerciseUtil.buildDto(exerciseRepository.save(exercise));
    }

    @Override
    public List<ExerciseDTO> getByBodyPartRefId(long bodyPartRefId) throws ServerException.EntityObjectNotFound {
        BodyPart bodyPart = bodyPartsRepository.findByRefId(bodyPartRefId);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTargetBodyPart(bodyPart);
        return exercises.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    @Transactional
    public void delete(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exerciseRepository.delete(exercise);
    }

    @Override
    @Transactional
    public void deleteAll(List<ExerciseDTO> exerciseDTOS) {
        List<Long> exerciseRefIds = exerciseDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList();
        log.info("Converted all the list of exercise IDs: {}", exerciseRefIds);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(exerciseRefIds);
        log.info("Fetched all the exercise data from the database: {}", exercises);
        exercises.forEach(exercise -> {
            exercise.setEquipments(Collections.emptyList());
            exercise.setResources(Collections.emptyList());
            exercise.setTargetMuscles(Collections.emptyList());
        });
        exerciseRepository.saveAll(exercises);
        exerciseRepository.deleteAll(exercises);
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadataRefId) {
        Training training = trainingRepository.findByRefId(trainingMetadataRefId);
        if (training == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTraining(training);
        return exercises.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId, long targetBodyPartRefId) {
        Training training = trainingRepository.findByRefId(trainingMetadatRefId);
        if (training == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        BodyPart bodyPart = bodyPartsRepository.findByRefId(targetBodyPartRefId);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        List<Exercise> exercises = exerciseRepository.findByTrainingAndTargetBodyPart(training, bodyPart);
        return exercises.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAll() {
        return exerciseRepository.findAll().stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    public void deleteAll() {
        exerciseRepository.deleteAll();
    }

    @Override
    @Transactional
    public void updateResource(ExerciseDTO exerciseDTO, List<MultipartFile> files) {
        List<FitmateResource> resources = new ArrayList<>();
        files.forEach(file -> {
            String fileName = KeyGeneratorUtil.uuid();
            String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            fileName = fileName + extension;
            String resourceId;
            try {
                resourceId = resourceService.save(file, fileName);
            } catch (IOException e) {
                throw new ServerException().new InternalError(e.getMessage());
            }
            FitmateResource fitmateResource = FitmateResource.builder()
                    .fileName(fileName)
                    .contentType(file.getContentType())
                    .resourceId(resourceId)
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(fileName)
                    .extension(extension)
                    .build();
//            fitmateResource = fitmateResourceRepository.save(fitmateResource);
            resources.add(fitmateResource);
        });
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) {
            throw new ServerException().new EntityObjectNotFound("Exercise object is not found.");
        }
        List<FitmateResource> existingResources = exercise.getResources();
        if (CollectionUtils.isEmpty(existingResources)) {
            exercise.setResources(resources);
        } else {
            existingResources.addAll(resources);
            exercise.setResources(existingResources);
        }
        exerciseRepository.save(exercise);
    }

    @Override
    public List<Map<String, Object>> findAllResources(long refId) {
        List<Map<String, Object>> files = new ArrayList<>();
        Exercise exercise = exerciseRepository.findByRefId(refId);
        List<GridFSFile> gridFSFiles = exercise.getResources()
                .stream()
                .map(FitmateResource::getResourceId)
                .toList()
                .stream()
                .map(resourceService::findGridFile)
                .toList();
        gridFSFiles.forEach(file -> {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", file.getObjectId().toString());
            fileInfo.put("filename", file.getFilename());
            fileInfo.put("contentType", file.getMetadata() != null ? file.getMetadata().getString("_contentType") : null);
            fileInfo.put("length", file.getLength());
            fileInfo.put("uploadDate", file.getUploadDate());
            fileInfo.put("downloadUrl", "/files/" + file.getObjectId().toString());
            files.add(fileInfo);
        });
        return files;
    }

    @Override
    public Optional<GridFsResource> findResource(long refId, String resourceId) {
        Exercise exercise = exerciseRepository.findByRefId(refId);
        FitmateResource fitmateResource = null;
        if (StringUtils.isEmpty(resourceId)) {
            if (CollectionUtils.isNotEmpty(exercise.getResources())) {
                fitmateResource = exercise.getResources().get(0);
            } else {
                throw new ServerException().new InternalError("Resource not found.");
            }
        } else {
            fitmateResource = exercise.getResources().stream().filter(resource -> StringUtils.equals(resourceId, resource.getResourceId())).findAny().orElseThrow(() -> new ServerException().new InternalError("Resource not found."));
        }
        return resourceService.find(fitmateResource.getResourceId());
    }

    @Override
    @Transactional
    public void updateResource(ExerciseDTO exerciseDTO, List<MultipartFile> files, String placeholder) {
        if (StringUtils.isEmpty(placeholder)) {
            throw new ServerException().new InternalError("placeholder is required.");
        }
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) {
            throw new ServerException().new EntityObjectNotFound("Exercise object is not found.");
        }
        List<FitmateResource> fitmateResources = exercise.getResources();
        fitmateResources = CollectionUtils.isEmpty(fitmateResources) ? new ArrayList<>() : fitmateResources;
        FitmateResource fitmateResource = fitmateResources
                .stream()
                .filter(fr -> StringUtils.equalsIgnoreCase(placeholder, fr.getPlaceholder()))
                .findAny()
                .orElse(null);
        if (fitmateResource == null) {
            for (MultipartFile file : files) {
                String fileName = KeyGeneratorUtil.uuid();
                String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                fileName = fileName + extension;
                String resourceId;
                try {
                    resourceId = resourceService.save(file, fileName);
                } catch (IOException e) {
                    throw new ServerException().new InternalError(e.getMessage());
                }
                FitmateResource fitmateResource1 = FitmateResource.builder()
                        .fileName(fileName)
                        .contentType(file.getContentType())
                        .resourceId(resourceId)
                        .refId(KeyGeneratorUtil.refId())
                        .placeholder(placeholder)
                        .uuid(fileName)
                        .extension(extension)
                        .build();
                fitmateResources.add(fitmateResource1);
                exercise.setResources(fitmateResources);
                exerciseRepository.save(exercise);
            }
        } else {
            for (MultipartFile file : files) {
                resourceService.remove(fitmateResource.getResourceId());
                String fileName = KeyGeneratorUtil.uuid();
                String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                fileName = fileName + extension;
                String resourceId;
                try {
                    resourceId = resourceService.save(file, fileName);
                } catch (IOException e) {
                    throw new ServerException().new InternalError(e.getMessage());
                }
                fitmateResource.setFileName(fileName);
                fitmateResource.setContentType(file.getContentType());
                fitmateResource.setResourceId(resourceId);
                fitmateResource.setPlaceholder(placeholder);
                fitmateResource.setExtension(extension);
                exerciseRepository.save(exercise);
            }
        }
    }

    @Override
    public Optional<GridFsResource> findResource(long refId, String resourceId, String placeholder) {
        Exercise exercise = exerciseRepository.findByRefId(refId);
        FitmateResource fitmateResource;
        if (StringUtils.isNotEmpty(resourceId)) {
            fitmateResource = exercise.getResources()
                    .stream()
                    .filter(resource -> StringUtils.equals(resourceId, resource.getResourceId()))
                    .findAny()
                    .orElseThrow(() -> new ServerException().new InternalError("Resource not found."));
        } else {
            fitmateResource = exercise.getResources()
                    .stream()
                    .filter(resource -> StringUtils.equalsIgnoreCase(placeholder, resource.getPlaceholder()))
                    .findAny()
                    .orElseThrow(() -> new ServerException().new InternalError("Resource not found."));
        }
        return resourceService.find(fitmateResource.getResourceId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> search(ExerciseSearchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return exerciseRepository.findAll().stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
        }
        Specification<Exercise> spec = Specification.where(null);
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid())) : spec;
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getName()) ? spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")) : spec;
        spec = StringUtils.isNotEmpty(filter.getUnit()) ? spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("unit")), filter.getUnit().toLowerCase())) : spec;
        spec = StringUtils.isNotEmpty(filter.getTrainingRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("training").get("refId"), Long.parseLong(filter.getTrainingRefId()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getTargetBodyPartRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("targetBodyPart").get("refId"), Long.parseLong(filter.getTargetBodyPartRefId()))) : spec;
        return exerciseRepository.findAll(spec).stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAllWithAnalytics() {
        List<Exercise> exercises = exerciseRepository.findAll();
        List<ExerciseDTO> dtoList = exercises.stream().map(FitmateServiceUtil.ExerciseUtil::buildDto).toList();
        dtoList.forEach(dto -> {
            ExerciseAnalyticsDTO exerciseAnalyticsDTO = ExerciseAnalyticsDTO.builder().build();
            Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getRefId()));
            List<Drill> drills = drillRepository.findByExerciseOrderByCrtnDateDesc(exercise);
            drills = drills.stream().filter(drill -> drill.getCrtnDate().isBefore(LocalDateTime.now().minusDays(5))).toList();
            exerciseAnalyticsDTO.setLastFiveDrills(drills.stream().map(FitmateServiceUtil.DrillUtil::buildDTO).collect(Collectors.toList()));
            exerciseAnalyticsDTO.setTotalNumberOfTimesCompleted(drills.size());
            dto.setAnalyticsDTOList(exerciseAnalyticsDTO);
        });
        return dtoList;
    }
}
