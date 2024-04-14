package com.abhi.leximentor.fitmate.controller;


import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.TrainingMetadataDTO;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.BodyPartService;
import com.abhi.leximentor.fitmate.service.ExerciseService;
import com.abhi.leximentor.fitmate.service.TrainingMetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final TrainingMetaDataService trainingMetaDataService;
    private final BodyPartService bodyPartService;

    @PostMapping(value = UrlConstants.ExerciseUrl.EXERCISE_ADD_ALL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addAll(@RequestBody List<ExerciseDTO> request) {
        try {
            List<ExerciseDTO> response = exerciseService.addAll(request);
            if (CollectionUtils.isNotEmpty(response)) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @PostMapping(value = UrlConstants.ExerciseUrl.EXERCISE_ADD, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody ExerciseDTO request,@RequestParam(required = true) String trainingMetadataRefId,@RequestParam(required = true) String targetBodyPartName) {
        try {
            log.info("Received a request to create a single exercise. Training ID:{},Target body part:{}",trainingMetadataRefId,targetBodyPartName);
            TrainingMetadataDTO trainingMetadataDTO=trainingMetaDataService.getByRefId(Long.parseLong(trainingMetadataRefId));
            if(trainingMetadataDTO==null){
                log.error("The training metadata object doesn't exist in our database.");
                return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
            }
            BodyPartsDTO targetBodyPartDTO=bodyPartService.getByName(targetBodyPartName);
            if(targetBodyPartDTO==null){
                log.error("The target body part doesn't exist in our database.");
                return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
            }
            request.setTrainingMetadata(trainingMetadataDTO);
            request.setTargetBodyPart(targetBodyPartDTO);
            List<ExerciseDTO> requests=new LinkedList<>();
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
        try {
            exerciseService.deleteAll(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }


}
