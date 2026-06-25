package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.constants.Unit;
import com.abhi.leximentor.fitmate.dto.ExerciseAnalyticsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.dto.filters.ExerciseSearchFilter;
import com.abhi.leximentor.fitmate.entities.*;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.mapper.DrillMapper;
import com.abhi.leximentor.fitmate.mapper.ExerciseMapper;
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
import org.springframework.data.domain.Pageable;
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
    private final ExerciseMapper exerciseMapper;
    private final DrillMapper drillMapper;

    @Override
    @Transactional
    public List<ExerciseDTO> addAll(List<ExerciseDTO> exerciseDTOS) throws ServerException.EntityObjectNotFound {
        List<Exercise> exercises = new LinkedList<>();
        for (ExerciseDTO exerciseDTO : exerciseDTOS) {
            Training training = StringUtils.isNotEmpty(exerciseDTO.getTraining().getName())
                    ? trainingRepository.findByNameIgnoreCase(exerciseDTO.getTraining().getName())
                    : trainingRepository.findByRefId(Long.parseLong(exerciseDTO.getTraining().getRefId()));
            if (training == null) {
                log.error("The training object is not found.");
                throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND + " {Training Object.} ");
            }
            BodyPart bodyPart = StringUtils.isNotEmpty(exerciseDTO.getBodyPart().getName())
                    ? bodyPartsRepository.findByName(exerciseDTO.getBodyPart().getName())
                    : bodyPartsRepository.findByRefId(Long.parseLong(exerciseDTO.getBodyPart().getRefId()));
            if (bodyPart == null) {
                log.error("The body part object is not found.");
                throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND + "{Body part object}");
            }
            List<Muscle> muscles = null;
            if (CollectionUtils.isNotEmpty(exerciseDTO.getTargetMuscles())) {
                muscles = muscleRepository.findByNameInIgnoreCase(exerciseDTO.getTargetMuscles().stream()
                        .filter(m -> StringUtils.isNotEmpty(m.getName()))
                        .map(m -> m.getName().toLowerCase(Locale.ROOT))
                        .toList());
            }
            exercises.add(exerciseMapper.toEntity(exerciseDTO, training, bodyPart, muscles));
        }

        List<Exercise> response = exerciseRepository.saveAll(exercises);
        List<ExerciseDTO> output = new LinkedList<>();
        response.forEach(exercise -> {
            try {
                output.add(exerciseMapper.toDto(exercise));
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
        return exerciseMapper.toDto(exercise);
    }

    @Override
    public List<ExerciseDTO> getAllByRefId(List<Long> refIds) throws ServerException.EntityObjectNotFound, ServerException.InternalError {
        if (CollectionUtils.isEmpty(refIds))
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(refIds);
        if (exercises.size() != refIds.size())
            throw new ServerException().new EntityObjectNotFound(LogConstants.GENERIC_EXCEPTION);
        return exercises.stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    public ExerciseDTO getByName(String name) {
        Exercise exercise = exerciseRepository.findByName(name.toUpperCase());
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return exerciseMapper.toDto(exercise);
    }

    @Override
    @Transactional
    public ExerciseDTO update(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setUnit(Unit.from(exerciseDTO.getUnit()).getValue());
        exercise.setStatus(Status.ApplicationStatus.getStatus(exerciseDTO.getStatus()));
        BodyPart targetBodyPart = bodyPartsRepository.findByNameIgnoreCase(exerciseDTO.getBodyPart().getName());
        if (targetBodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        exercise.setTargetBodyPart(targetBodyPart);
        exercise.setEquipments(null);
        exercise.setEquipments(exerciseDTO.getEquipments());
        return exerciseMapper.toDto(exerciseRepository.save(exercise));
    }

    @Override
    public List<ExerciseDTO> getByBodyPartRefId(long bodyPartRefId) throws ServerException.EntityObjectNotFound {
        BodyPart bodyPart = bodyPartsRepository.findByRefId(bodyPartRefId);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return exerciseRepository.findByTargetBodyPart(bodyPart).stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void delete(ExerciseDTO exerciseDTO) throws ServerException.EntityObjectNotFound {
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        deleteExercisesByDbIds(List.of(exercise.getId()));
    }

    @Override
    @Transactional
    public void deleteAll(List<ExerciseDTO> exerciseDTOS) {
        List<Long> refIds = exerciseDTOS.stream().map(dto -> Long.parseLong(dto.getRefId())).toList();
        log.info("Deleting exercises with refIds: {}", refIds);
        List<Exercise> exercises = exerciseRepository.findByRefIdIn(refIds);
        if (exercises.isEmpty()) {
            log.warn("No exercises found for refIds: {}", refIds);
            return;
        }
        List<Long> dbIds = exercises.stream().map(Exercise::getId).toList();
        deleteExercisesByDbIds(dbIds);
        log.info("Deleted exercises with dbIds: {}", dbIds);
    }

    // Deletes exercises in FK-safe order using explicit native SQL to avoid JPA cascade issues.
    private void deleteExercisesByDbIds(List<Long> dbIds) {
        drillRepository.nullifyExerciseRefs(dbIds);          // 1. break drill → exercise FK
        exerciseRepository.deleteResourceLinks(dbIds);        // 2. join table: exercise_resources
        exerciseRepository.deleteMuscleLinks(dbIds);          // 3. join table: exercise_muscle
        exerciseRepository.deleteEquipmentLinks(dbIds);       // 4. element collection: equipments
        exerciseRepository.deleteByDbIds(dbIds);              // 5. main row
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefId(long trainingMetadataRefId) {
        Training training = trainingRepository.findByRefId(trainingMetadataRefId);
        if (training == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return exerciseRepository.findByTraining(training).stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(long trainingMetadatRefId, long targetBodyPartRefId) {
        Training training = trainingRepository.findByRefId(trainingMetadatRefId);
        if (training == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        BodyPart bodyPart = bodyPartsRepository.findByRefId(targetBodyPartRefId);
        if (bodyPart == null) throw new ServerException().new EntityObjectNotFound(LogConstants.ENTITY_NOT_FOUND);
        return exerciseRepository.findByTrainingAndTargetBodyPart(training, bodyPart).stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    public List<ExerciseDTO> getAll() {
        return exerciseRepository.findAll().stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    public PagedResponse<ExerciseDTO> getAll(Pageable pageable) {
        return PagedResponse.of(exerciseRepository.findAll(pageable), exerciseMapper::toDto);
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
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            fileName = fileName + extension;
            String resourceId;
            try {
                resourceId = resourceService.save(file, fileName);
            } catch (IOException e) {
                throw new ServerException().new InternalError(e.getMessage());
            }
            resources.add(FitmateResource.builder()
                    .fileName(fileName)
                    .contentType(file.getContentType())
                    .resourceId(resourceId)
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(fileName)
                    .extension(extension)
                    .build());
        });
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound("Exercise object is not found.");
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
        exercise.getResources().stream()
                .map(FitmateResource::getResourceId)
                .map(resourceService::findGridFile)
                .forEach((GridFSFile file) -> {
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
        FitmateResource fitmateResource;
        if (StringUtils.isEmpty(resourceId)) {
            if (CollectionUtils.isNotEmpty(exercise.getResources())) {
                fitmateResource = exercise.getResources().get(0);
            } else {
                throw new ServerException().new InternalError("Resource not found.");
            }
        } else {
            fitmateResource = exercise.getResources().stream()
                    .filter(r -> StringUtils.equals(resourceId, r.getResourceId()))
                    .findAny()
                    .orElseThrow(() -> new ServerException().new InternalError("Resource not found."));
        }
        return resourceService.find(fitmateResource.getResourceId());
    }

    @Override
    @Transactional
    public void updateResource(ExerciseDTO exerciseDTO, List<MultipartFile> files, String placeholder) {
        if (StringUtils.isEmpty(placeholder))
            throw new ServerException().new InternalError("placeholder is required.");
        Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(exerciseDTO.getRefId()));
        if (exercise == null) throw new ServerException().new EntityObjectNotFound("Exercise object is not found.");
        List<FitmateResource> fitmateResources = CollectionUtils.isEmpty(exercise.getResources())
                ? new ArrayList<>() : exercise.getResources();
        FitmateResource fitmateResource = fitmateResources.stream()
                .filter(fr -> StringUtils.equalsIgnoreCase(placeholder, fr.getPlaceholder()))
                .findAny().orElse(null);
        if (fitmateResource == null) {
            for (MultipartFile file : files) {
                String fileName = KeyGeneratorUtil.uuid();
                String extension = Objects.requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf("."));
                fileName = fileName + extension;
                String resourceId;
                try {
                    resourceId = resourceService.save(file, fileName);
                } catch (IOException e) {
                    throw new ServerException().new InternalError(e.getMessage());
                }
                fitmateResources.add(FitmateResource.builder()
                        .fileName(fileName).contentType(file.getContentType())
                        .resourceId(resourceId).refId(KeyGeneratorUtil.refId())
                        .placeholder(placeholder).uuid(fileName).extension(extension).build());
                exercise.setResources(fitmateResources);
                exerciseRepository.save(exercise);
            }
        } else {
            for (MultipartFile file : files) {
                resourceService.remove(fitmateResource.getResourceId());
                String fileName = KeyGeneratorUtil.uuid();
                String extension = Objects.requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf("."));
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
            fitmateResource = exercise.getResources().stream()
                    .filter(r -> StringUtils.equals(resourceId, r.getResourceId())).findAny().orElse(null);
        } else {
            fitmateResource = exercise.getResources().stream()
                    .filter(r -> StringUtils.equalsIgnoreCase(placeholder, r.getPlaceholder())).findAny().orElse(null);
        }
        if (fitmateResource == null) return Optional.empty();
        return resourceService.find(fitmateResource.getResourceId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> search(ExerciseSearchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return exerciseRepository.findAll().stream().map(exerciseMapper::toDto).toList();
        }
        return exerciseRepository.findAll(buildExerciseSpec(filter)).stream().map(exerciseMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExerciseDTO> search(ExerciseSearchFilter filter, Pageable pageable) {
        Specification<Exercise> spec = (filter == null || filter.isEmpty())
                ? Specification.where(null)
                : buildExerciseSpec(filter);
        return PagedResponse.of(exerciseRepository.findAll(spec, pageable), exerciseMapper::toDto);
    }

    private Specification<Exercise> buildExerciseSpec(ExerciseSearchFilter filter) {
        Specification<Exercise> spec = Specification.where(null);
        spec = StringUtils.isNotEmpty(filter.getUuid()) ? spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid())) : spec;
        spec = StringUtils.isNotEmpty(filter.getRefId()) ? spec.and((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getName()) ? spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")) : spec;
        spec = StringUtils.isNotEmpty(filter.getUnit()) ? spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("unit")), filter.getUnit().toLowerCase())) : spec;
        spec = StringUtils.isNotEmpty(filter.getTrainingRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("training").get("refId"), Long.parseLong(filter.getTrainingRefId()))) : spec;
        spec = StringUtils.isNotEmpty(filter.getTargetBodyPartRefId()) ? spec.and((root, query, cb) -> cb.equal(root.join("targetBodyPart").get("refId"), Long.parseLong(filter.getTargetBodyPartRefId()))) : spec;
        return spec;
    }

    @Override
    public List<ExerciseDTO> getAllWithAnalytics() {
        List<Exercise> exercises = exerciseRepository.findAll();
        List<ExerciseDTO> dtoList = exercises.stream().map(exerciseMapper::toDto).toList();
        dtoList.forEach(dto -> {
            ExerciseAnalyticsDTO exerciseAnalyticsDTO = ExerciseAnalyticsDTO.builder().build();
            Exercise exercise = exerciseRepository.findByRefId(Long.parseLong(dto.getRefId()));
            List<Drill> drills = drillRepository.findByExerciseOrderByCrtnDateDesc(exercise);
            drills = drills.stream()
                    .filter(drill -> drill.getCrtnDate().isBefore(LocalDateTime.now().minusDays(5)))
                    .toList();
            exerciseAnalyticsDTO.setLastFiveDrills(drills.stream().map(drillMapper::toDto).collect(Collectors.toList()));
            exerciseAnalyticsDTO.setTotalNumberOfTimesCompleted(drills.size());
            dto.setAnalyticsDTOList(exerciseAnalyticsDTO);
        });
        return dtoList;
    }
}
