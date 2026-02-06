package com.abhi.leximentor.inventory.controller.rest.analytics;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTrendsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillTypePerformanceDTO;
import com.abhi.leximentor.inventory.dto.analytics.UserPerformanceDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.analytics.AnalyticsFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillAnalyticsController {
    private final AnalyticsFacade analyticsFacade;

    @GetMapping(value = "/api/leximentor/analytics/drill/{drillRefId}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillAnalyticsData(@PathVariable String drillRefId,
                                                                 @RequestParam(name = "topN", required = false, defaultValue = "10") int topN) {
        log.info("Drill analytics requested. drillRefId={}, topN={}", drillRefId, topN);
        DrillAnalyticsDTO response = analyticsFacade.getDrillAnalytics(Long.parseLong(drillRefId), topN);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/api/leximentor/analytics/drill/challenge/metadata", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillOverallChallengeAnalytics() {
        log.info("Drill challenge metadata analytics requested");
        List<DrillChallengeAnalyticsDTO> drillChallengeAnalyticsDTOList = analyticsFacade.getDrillChallengeAnalytics();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeAnalyticsDTOList);
    }

    @GetMapping(value = "/api/leximentor/analytics/drill/type/summary", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillTypeSummary() {
        log.info("Drill type summary requested");
        List<DrillTypePerformanceDTO> summary = analyticsFacade.getDrillTypeSummary();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, summary);
    }

    @GetMapping(value = "/api/leximentor/analytics/drill/trends", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillTrends(@RequestParam(name = "days", required = false, defaultValue = "30") int days,
                                                         @RequestParam(name = "username", required = false) String username) {
        log.info("Drill trends requested. days={}, username={}", days, username);
        DrillTrendsDTO trends = analyticsFacade.getDrillTrends(days, username);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, trends);
    }

    @GetMapping(value = "/api/leximentor/analytics/drill/user/performance", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getUserPerformance(@RequestParam(name = "username") String username,
                                                              @RequestParam(name = "topN", required = false, defaultValue = "3") int topN) {
        log.info("User performance requested. username={}, topN={}", username, topN);
        UserPerformanceDTO performance = analyticsFacade.getUserPerformance(username, topN);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, performance);
    }
}
