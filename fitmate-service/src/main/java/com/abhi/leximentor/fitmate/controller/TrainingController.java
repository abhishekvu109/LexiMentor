package com.abhi.leximentor.fitmate.controller;


import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.dto.TrainingDTO;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_ADD, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<TrainingDTO> request) {
        try {
            List<TrainingDTO> response = trainingService.addAll(request);
            if (CollectionUtils.isNotEmpty(response)) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_GET_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String trainingMetadataRefId) {
        try {
            TrainingDTO response = trainingService.getByRefId(Long.parseLong(trainingMetadataRefId));
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_GET, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByName(@RequestParam String name) {
        try {
            TrainingDTO response = trainingService.getByName(name);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_GET_ALL, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PagedResponse<TrainingDTO> response = trainingService.getAll(PageRequest.of(page, size));
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @PutMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_UPDATE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody TrainingDTO request) {
        try {
            TrainingDTO response = trainingService.update(request);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @DeleteMapping(value = UrlConstants.TrainingMetadataUrl.TRAINING_METADATA_DELETE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<TrainingDTO> request) {
        try {
            trainingService.deleteAll(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }
}
