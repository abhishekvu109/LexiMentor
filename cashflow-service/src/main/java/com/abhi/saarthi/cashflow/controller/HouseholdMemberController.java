package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.HouseholdMemberDTO;
import com.abhi.saarthi.cashflow.model.HouseholdMemberSearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.HouseholdMemberService;
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
@RequestMapping("/api/cashflow/households/household-members")
public class HouseholdMemberController {

    private final HouseholdMemberService householdMemberService;

    @PostMapping(value = "/household-member", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<HouseholdMemberDTO> request) {
        log.info("Received a request to create household member: {}", request);
        List<HouseholdMemberDTO> response = householdMemberService.add(request);
        log.info("Successfully created household member: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = "/household-member", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<HouseholdMemberDTO> request) {
        log.info("Received a request to update household member: {}", request);
        List<HouseholdMemberDTO> response = householdMemberService.update(request);
        log.info("Successfully updated household member: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = "/household-member/{refId}")
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@PathVariable String refId) {
        log.info("Received a request to delete household member by refId: {}", refId);
        householdMemberService.delete(Long.parseLong(refId));
        log.info("Successfully deleted household member with refId: {}", refId);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted.");
    }

    @GetMapping(value = "/household-member/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> find(@PathVariable String refId) {
        log.info("Received a request to find household member by refId: {}", refId);
        HouseholdMemberDTO response = householdMemberService.findByRefId(Long.parseLong(refId));
        log.info("Successfully found household member: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PostMapping(value = "/household-member/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody HouseholdMemberSearchFilter filter) {
        log.info("Received a request to search household member with filter: {}", filter);
        List<HouseholdMemberDTO> response = householdMemberService.search(filter);
        log.info("Successfully found household members: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
