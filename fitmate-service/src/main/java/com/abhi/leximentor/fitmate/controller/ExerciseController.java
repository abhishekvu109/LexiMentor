package com.abhi.leximentor.fitmate.controller;


import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.TrainingDTO;
import com.abhi.leximentor.fitmate.dto.filters.ExerciseSearchFilter;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.BodyPartService;
import com.abhi.leximentor.fitmate.service.ExerciseService;
import com.abhi.leximentor.fitmate.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final TrainingService trainingService;
    private final BodyPartService bodyPartService;

    @PostMapping(value = UrlConstants.ExerciseUrl.EXERCISE_ADD_ALL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addAll(@RequestBody List<ExerciseDTO> request) {
        List<ExerciseDTO> response = exerciseService.addAll(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = UrlConstants.ExerciseUrl.EXERCISE_DELETE_ALL, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteAll() {
        exerciseService.deleteAll();
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "All exercises have been removed. ");
    }

    @PostMapping(value = UrlConstants.ExerciseUrl.EXERCISE_ADD, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody ExerciseDTO request, @RequestParam(required = true) String trainingMetadataRefId, @RequestParam(required = true) String targetBodyPartName) {
        try {
            log.info("Received a request to create a single exercise. Training ID:{},Target body part:{}", trainingMetadataRefId, targetBodyPartName);
            TrainingDTO trainingDTO = trainingService.getByRefId(Long.parseLong(trainingMetadataRefId));
            if (trainingDTO == null) {
                log.error("The training metadata object doesn't exist in our database.");
                return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
            }
            BodyPartsDTO targetBodyPartDTO = bodyPartService.getByName(targetBodyPartName);
            if (targetBodyPartDTO == null) {
                log.error("The target body part doesn't exist in our database.");
                return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
            }
            request.setTraining(trainingDTO);
            request.setBodyPart(targetBodyPartDTO);
            List<ExerciseDTO> requests = new LinkedList<>();
            requests.add(request);
            List<ExerciseDTO> response = exerciseService.addAll(requests);
            if (CollectionUtils.isNotEmpty(response)) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = UrlConstants.ExerciseUrl.EXERCISE_GET_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String exerciseRefId) {
        try {
            ExerciseDTO response = exerciseService.getByRefId(Long.parseLong(exerciseRefId));
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = UrlConstants.ExerciseUrl.EXERCISE_GET, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByTargetBodyPart(@RequestParam(required = false) String name, @RequestParam(required = false) String bodyPartRefId, @RequestParam(required = false) String trainingMetadataRefId) {
        try {
            List<ExerciseDTO> response = new LinkedList<>();
            if (StringUtils.isNotEmpty(bodyPartRefId) && StringUtils.isNotEmpty(trainingMetadataRefId)) {
                response = exerciseService.getAllByTrainingMetadataRefIdAndTragetBodyPartRefId(Long.parseLong(trainingMetadataRefId), Long.parseLong(bodyPartRefId));
            } else if (StringUtils.isNotEmpty(name)) {
                ExerciseDTO responseDTO = exerciseService.getByName(name);
                if (responseDTO != null) response.add(responseDTO);
            } else if (StringUtils.isNotEmpty(bodyPartRefId)) {
                response = exerciseService.getByBodyPartRefId(Long.parseLong(bodyPartRefId));
            } else if (StringUtils.isNotEmpty(trainingMetadataRefId)) {
                response = exerciseService.getAllByTrainingMetadataRefId(Long.parseLong(trainingMetadataRefId));
            } else {
                response = exerciseService.getAll();
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (Exception ex) {
//            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");

        }
    }


    @PutMapping(value = UrlConstants.ExerciseUrl.EXERCISE_UPDATE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody ExerciseDTO request) {
        try {
            ExerciseDTO response = exerciseService.update(request);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @DeleteMapping(value = UrlConstants.ExerciseUrl.EXERCISE_DELETE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<ExerciseDTO> request) {
        log.info("Received a request to delete the exercise data : {}", request);
        exerciseService.deleteAll(request);
        log.info("The data has been removed.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
    }

    @GetMapping(value = UrlConstants.ExerciseUrl.EXERCISE_GET_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> findAll() {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, exerciseService.getAll());
    }

    @PutMapping(value = UrlConstants.ExerciseUrl.EXERCISE_UPDATE_RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> uploadResources(@RequestParam(name = "files") List<MultipartFile> files,
                                                                         @RequestParam(name = "refId") String refId,
                                                                         @RequestParam(name = "placeholder", required = false) String placeholder) {
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().refId(refId).build();
        exerciseService.updateResource(exerciseDTO, files, placeholder);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully updated the resources.");
    }

    @GetMapping(value = UrlConstants.ExerciseUrl.EXERCISE_GET_RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getResources(@RequestParam("refId") String refId) {
        List<Map<String, Object>> resources = exerciseService.findAllResources(Long.parseLong(refId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, resources);
    }

    @GetMapping(value = UrlConstants.ExerciseUrl.EXERCISE_GET_RESOURCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GridFsResource> getResource(@RequestParam("resourceId") String resourceId,
                                                      @RequestParam("refId") String refId,
                                                      @RequestParam(name = "placeholder", required = false) String placeholder) {
        return exerciseService.findResource(Long.parseLong(refId), resourceId, placeholder)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(resource.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/api/fitmate/exercises/exercise/search", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody(required = false) ExerciseSearchFilter filter) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, exerciseService.search(filter));
    }
}
