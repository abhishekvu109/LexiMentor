package com.abhi.leximentor.inventory.controller.rest.analytics;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.analytics.WordAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDistributionDTO;
import com.abhi.leximentor.inventory.dto.analytics.WordDifficultyDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.analytics.AnalyticsFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordAnalyticsController {
    private final AnalyticsFacade analyticsFacade;

    @GetMapping(value = UrlConstants.Analytics.Words.WORDS_OVERVIEW, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordAnalyticsOverview() {
        WordAnalyticsDTO response = analyticsFacade.getWordAnalyticsOverview();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Analytics.Words.WORDS_DISTRIBUTION, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordDistribution() {
        WordDistributionDTO response = analyticsFacade.getWordDistribution();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Analytics.Words.WORDS_DIFFICULTY, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getWordDifficultyHeatmap(@RequestParam(name = "topN", required = false, defaultValue = "20") int topN) {
        List<WordDifficultyDTO> response = analyticsFacade.getWordDifficultyHeatmap(topN);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
