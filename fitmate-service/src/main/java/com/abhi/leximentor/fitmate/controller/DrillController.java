package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.DrillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/fitmate/drills/drill")
public class DrillController {

    private final DrillService drillService;


    @GetMapping(value = UrlConstants.Drill.DRILL_FIND_BY_EXERCISE_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> findByExerciseName(@PathVariable String exerciseName) {
        List<DrillDTO> drills = drillService.findByExerciseNameOrderByCrtnDate(exerciseName);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drills);
    }


    @PutMapping(consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody DrillDTO dto) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillService.update(dto));
    }

    @DeleteMapping
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody(required = true) DrillDTO drillDTO) {
        drillService.delete(drillDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted Successfully.");
    }

    @PostMapping
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody DrillDTO drillDTO) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillService.add(drillDTO));
    }

}
