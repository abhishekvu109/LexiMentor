package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.CategoryDTO;
import com.abhi.saarthi.cashflow.model.CategorySearchFilter;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.CategoryService;
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
@RequestMapping("/api/cashflow/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/category", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<CategoryDTO> request) {
        log.info("Received a request to create category: {}", request);
        List<CategoryDTO> response = categoryService.add(request);
        log.info("Successfully created category: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = "/category", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody List<CategoryDTO> request) {
        log.info("Received a request to update category: {}", request);
        List<CategoryDTO> response = categoryService.update(request);
        log.info("Successfully updated category: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = "/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody List<CategoryDTO> request) {
        log.info("Received a request to delete category: {}", request);
        categoryService.delete(request);
        log.info("Successfully deleted category: {}", request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted.");
    }

    @GetMapping(value = "/category/{refId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> find(@PathVariable String refId) {
        log.info("Received a request to find category by refId: {}", refId);
        CategoryDTO response = categoryService.findByRefId(Long.parseLong(refId));
        log.info("Successfully found category: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/category/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> search(@RequestBody CategorySearchFilter filter) {
        log.info("Received a request to search category with filter: {}", filter);
        List<CategoryDTO> response = categoryService.search(filter);
        log.info("Successfully found categories: {}", response);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
