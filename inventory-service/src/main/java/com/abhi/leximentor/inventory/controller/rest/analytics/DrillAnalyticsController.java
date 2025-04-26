package com.abhi.leximentor.inventory.controller.rest.analytics;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.analytics.DrillAnalyticsDTO;
import com.abhi.leximentor.inventory.dto.analytics.DrillChallengeAnalyticsDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.analytics.DrillAnalyticsService;
import com.abhi.leximentor.inventory.service.analytics.DrillChallengeAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillAnalyticsController {
    private final DrillAnalyticsService drillAnalyticsService;
    private final DrillChallengeAnalyticsService drillChallengeAnalyticsService;

    @GetMapping(value = UrlConstants.Drill.DrillAnalytics.DRILL_GET_ANALYTICS_DRILL_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillAnalyticsData(@PathVariable String drillRefId) {
        DrillAnalyticsDTO response = drillAnalyticsService.getDrillAnalyticsData(Long.parseLong(drillRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Drill.DrillAnalytics.DRILL_GET_CHALLENGE_METADATA_ANALYTICS_DRILL_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> getDrillOverallChallengeAnalytics() {
        List<DrillChallengeAnalyticsDTO> drillChallengeAnalyticsDTOList = drillChallengeAnalyticsService.getDrillChallengeMetadataAnalytics();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeAnalyticsDTOList);
    }
}
