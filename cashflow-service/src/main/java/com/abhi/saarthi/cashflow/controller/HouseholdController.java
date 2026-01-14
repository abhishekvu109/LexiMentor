package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.HouseholdService;
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
@RequestMapping("/api/cashflow/households")
public class HouseholdController {

    private final HouseholdService householdService;

    @PostMapping(value = "/household", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<HouseholdDTO> request) {
        log.info("Received a request to create household: {}", request);
        List<HouseholdDTO> response = householdService.add(request);
        log.info("Successfully created household: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = "/household", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<HouseholdDTO> request) {
        log.info("Received a request to update household: {}", request);
        List<HouseholdDTO> response = householdService.update(request);
        log.info("Successfully updated household: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = "/household", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<HouseholdDTO> request) {
        log.info("Received a request to delete household: {}", request);
        householdService.delete(request);
        log.info("Successfully deleted household: {}", request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted.");
    }

    @GetMapping(value = "/household/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> find(@PathVariable String refId) {
        log.info("Received a request to find household by refId: {}", refId);
        HouseholdDTO response = householdService.findByRefId(Long.parseLong(refId));
        log.info("Successfully found household: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PostMapping(value = "/household/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody HouseholdSearchFilter filter) {
        log.info("Received a request to search household with filter: {}", filter);
        List<HouseholdDTO> response = householdService.search(filter);
        log.info("Successfully found households: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
