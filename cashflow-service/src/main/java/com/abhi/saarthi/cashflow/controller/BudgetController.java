package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.BudgetDTO;
import com.abhi.saarthi.cashflow.model.BudgetSearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.BudgetService;
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
@RequestMapping("/api/cashflow/households/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping(value = "/budget", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<BudgetDTO> request) {
        log.info("Received a request to create budget: {}", request);
        List<BudgetDTO> response = budgetService.add(request);
        log.info("Successfully created budget: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = "/budget", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<BudgetDTO> request) {
        log.info("Received a request to update budget: {}", request);
        List<BudgetDTO> response = budgetService.update(request);
        log.info("Successfully updated budget: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = "/budget", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<BudgetDTO> request) {
        log.info("Received a request to delete budget: {}", request);
        budgetService.delete(request);
        log.info("Successfully deleted budget: {}", request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted.");
    }

    @GetMapping(value = "/budget/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> find(@PathVariable String refId) {
        log.info("Received a request to find budget by refId: {}", refId);
        BudgetDTO response = budgetService.findByRefId(Long.parseLong(refId));
        log.info("Successfully found budget: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/budget/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody BudgetSearchFilter filter) {
        log.info("Received a request to search budget with filter: {}", filter);
        List<BudgetDTO> response = budgetService.search(filter);
        log.info("Successfully found budgets: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
