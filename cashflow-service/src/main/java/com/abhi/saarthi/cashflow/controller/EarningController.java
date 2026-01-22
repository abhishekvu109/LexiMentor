package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.EarningDTO;
import com.abhi.saarthi.cashflow.model.EarningSearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.EarningService;
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
@RequestMapping("/api/cashflow/earnings/earning")
public class EarningController {
    private final EarningService earningService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<EarningDTO> request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, earningService.add(request));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<EarningDTO> request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, earningService.update(request));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<EarningDTO> request) {
        earningService.delete(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted the earnings.");
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody(required = false) EarningSearchFilter filter) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, earningService.search(filter));
    }

    @GetMapping(value = "/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> get(@PathVariable String refId) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, earningService.findByRefId(Long.parseLong(refId)));
    }
}
