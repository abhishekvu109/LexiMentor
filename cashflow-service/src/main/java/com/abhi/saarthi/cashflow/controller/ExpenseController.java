package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.model.ExpenseSearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.ExpenseService;
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
@RequestMapping("/api/cashflow/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping(value = "/expense", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<ExpenseDTO> request) {
        log.info("Received a request to create expense: {}", request);
        List<ExpenseDTO> response = expenseService.add(request);
        log.info("Successfully created expense: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = "/expense", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<ExpenseDTO> request) {
        log.info("Received a request to update expense: {}", request);
        List<ExpenseDTO> response = expenseService.update(request);
        log.info("Successfully updated expense: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = "/expense", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<ExpenseDTO> request) {
        log.info("Received a request to delete expense: {}", request);
        expenseService.delete(request);
        log.info("Successfully deleted expense: {}", request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted.");
    }

    @GetMapping(value = "/expense/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> find(@PathVariable String refId) {
        log.info("Received a request to find expense by refId: {}", refId);
        ExpenseDTO response = expenseService.findByRefId(Long.parseLong(refId));
        log.info("Successfully found expense: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/expense/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody ExpenseSearchFilter filter) {
        log.info("Received a request to search expense with filter: {}", filter);
        List<ExpenseDTO> response = expenseService.search(filter);
        log.info("Successfully found expenses: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
