package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.dto.RoutineGenerationDTO;
import com.abhi.leximentor.fitmate.dto.filters.RoutineSearchFilter;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.RoutineGeneratorService;
import com.abhi.leximentor.fitmate.service.RoutineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(UrlConstants.RoutineUrl.BASE_URL)
public class RoutineController {
    private final RoutineService routineService;
    private final RoutineGeneratorService generateRoutine;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody RoutineDTO request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, routineService.addByNames(request));
    }

    @GetMapping(value = "/{routineRefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String routineRefId) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, routineService.getByRefId(Long.parseLong(routineRefId)));
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> get(@ModelAttribute RoutineSearchFilter filter) {
        List<RoutineDTO> result = routineService.search(filter);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }


    @PutMapping(consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody RoutineDTO request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, routineService.update(request));
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<RoutineDTO> request) {
        routineService.deleteAll(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> generateRoutine(@RequestBody RoutineGenerationDTO request) {
        RoutineDTO response = generateRoutine.generateRoutine(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }


    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody(required = false) RoutineSearchFilter filter) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, routineService.search(filter));
    }

}
