package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.constants.ApplicationConstants;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsRequest;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsBehaviorResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsCoreResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsDiagnosticResponse;
import com.abhi.saarthi.cashflow.dto.analytics.AnalyticsPlanningResponse;
import com.abhi.saarthi.cashflow.model.ResponseEntityBuilder;
import com.abhi.saarthi.cashflow.model.RestApiResponse;
import com.abhi.saarthi.cashflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/cashflow/households/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @PostMapping
    public @ResponseBody ResponseEntity<RestApiResponse> getHouseholdAnalytics(@RequestBody AnalyticsRequest request) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, analyticsService.analyze(request));
    }

    @PostMapping("/core")
    public @ResponseBody ResponseEntity<RestApiResponse> getHouseholdAnalyticsCore(@RequestBody AnalyticsRequest request) {
        AnalyticsCoreResponse result = analyticsService.analyzeCore(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }

    @PostMapping("/behavior")
    public @ResponseBody ResponseEntity<RestApiResponse> getHouseholdAnalyticsBehavior(@RequestBody AnalyticsRequest request) {
        AnalyticsBehaviorResponse result = analyticsService.analyzeBehavior(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }

    @PostMapping("/diagnostic")
    public @ResponseBody ResponseEntity<RestApiResponse> getHouseholdAnalyticsDiagnostic(@RequestBody AnalyticsRequest request) {
        AnalyticsDiagnosticResponse result = analyticsService.analyzeDiagnostic(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }

    @PostMapping("/planning")
    public @ResponseBody ResponseEntity<RestApiResponse> getHouseholdAnalyticsPlanning(@RequestBody AnalyticsRequest request) {
        AnalyticsPlanningResponse result = analyticsService.analyzePlanning(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, result);
    }
}


