package com.abhi.leximentor.leximentor.controller.rest.analytics;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.analytics.WordAnalyticsDTO;
import com.abhi.leximentor.leximentor.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.leximentor.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.analytics.AnalyticsFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/analytics/words", "/api/leximentor/analytics/words"})
public class WordAnalyticsController {
    private final AnalyticsFacade analyticsFacade;

    @GetMapping(value = "/overview", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordAnalyticsOverview() {
        log.info("Word analytics overview requested");
        WordAnalyticsDTO response = analyticsFacade.getWordAnalyticsOverview();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/distribution", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordDistribution() {
        log.info("Word distribution requested");
        WordDistributionDTO response = analyticsFacade.getWordDistribution();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/difficulty", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordDifficultyHeatmap(@RequestParam(name = "topN", required = false, defaultValue = "20") int topN) {
        log.info("Word difficulty heatmap requested. topN={}", topN);
        List<WordDifficultyDTO> response = analyticsFacade.getWordDifficultyHeatmap(topN);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
