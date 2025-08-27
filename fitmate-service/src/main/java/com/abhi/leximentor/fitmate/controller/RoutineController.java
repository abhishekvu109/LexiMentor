package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.LogConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineSearchFilter;
import com.abhi.leximentor.fitmate.exceptions.entities.ServerException;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoutineController {
    private final RoutineService routineService;

    @PostMapping(value = UrlConstants.RoutineUrl.ROUTINE_ADD, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody RoutineDTO request) {
        RoutineDTO response = routineService.addByNames(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.RoutineUrl.ROUTINE_GET_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String routineRefId) {
        RoutineDTO response = routineService.getByRefId(Long.parseLong(routineRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }


    @GetMapping(value = UrlConstants.RoutineUrl.ROUTINE_GET, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> get(@ModelAttribute RoutineSearchFilter filter) {
        List<RoutineDTO> result = routineService.search(filter);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }


    @PutMapping(value = UrlConstants.RoutineUrl.ROUTINE_UPDATE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody RoutineDTO request) {
        try {
            RoutineDTO response = routineService.update(request);
            if (response != null) {
                return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
            }
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }

    @DeleteMapping(value = UrlConstants.RoutineUrl.ROUTINE_DELETE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<RoutineDTO> request) {
        try {
            routineService.deleteAll(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
        } catch (Exception ex) {
            throw new ServerException().new InternalError(LogConstants.GENERIC_EXCEPTION);
        }
    }


}
