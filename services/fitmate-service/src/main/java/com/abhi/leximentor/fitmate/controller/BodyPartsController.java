package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.BodyPartService;
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
@RequestMapping("/api/fitmate/bodyparts")
public class BodyPartsController {
    private final BodyPartService bodyPartService;

    @PostMapping(value = "/bodypart", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<BodyPartsDTO> request) {
        log.info("Received a request to add a body part : {}", request);
        try {
            List<BodyPartsDTO> response = bodyPartService.addAll(request);
            if (CollectionUtils.isNotEmpty(response)) {
                log.info("Received a response : {}", response);
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            } else {
                log.error("The service has returned empty response");
                return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
            }
        } catch (Exception ex) {
            log.error("Some exception has occurred: {}", ex.getMessage());
            ex.getCause();
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(value = "/bodypart/{bodyPartRefId}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String bodyPartRefId) {
        try {
            BodyPartsDTO response = bodyPartService.getByRefId(Long.parseLong(bodyPartRefId));
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @GetMapping(produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PagedResponse<BodyPartsDTO> response = bodyPartService.getAll(PageRequest.of(page, size));
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @PostMapping(value = "/bodypart", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByName(@RequestParam String name) {
        try {
            BodyPartsDTO response = bodyPartService.getByName(name);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @PutMapping(value = "/bodypart", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody BodyPartsDTO request) {
        try {
            BodyPartsDTO response = bodyPartService.update(request);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @DeleteMapping(value = "/bodypart", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<BodyPartsDTO> request) {
        try {
            bodyPartService.deleteAll(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

}
