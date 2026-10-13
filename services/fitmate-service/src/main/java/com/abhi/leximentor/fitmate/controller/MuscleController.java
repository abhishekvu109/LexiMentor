package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.MuscleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/fitmate/muscles")
public class MuscleController {
    private final MuscleService muscleService;

    @PostMapping(value = "/muscle", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<MuscleDTO> request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, muscleService.addAll(request));
    }

    @DeleteMapping(value = "/muscle", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteAll() {
        muscleService.deleteAll();
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "All the BodyParts have been removed.");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<MuscleDTO> muscles = muscleService.findAll(PageRequest.of(page, size));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, muscles);
    }


}
