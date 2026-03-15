package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.constants.UrlConstants;
import com.abhi.leximentor.fitmate.dto.rec.RecommendationDTO;
import com.abhi.leximentor.fitmate.dto.rec.RecommendationRequestDTO;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(UrlConstants.Recommendation.BASE_URL)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Generate a personalised workout recommendation.
     *
     * <pre>
     * POST /api/fitmate/recommendations/recommend
     * {
     *   "username":            "john_doe",
     *   "targetBodyPartRefId": "1001",
     *   "trainingRefId":       "2001",   // optional
     *   "maxExercises":        5         // optional, default 5
     * }
     * </pre>
     */
    @PostMapping(value = UrlConstants.Recommendation.RECOMMEND,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> recommend(
            @RequestBody RecommendationRequestDTO request) {
        log.info("[RecommendationController] Request: user='{}' bodyPart='{}'",
                request.getUsername(), request.getTargetBodyPartRefId());
        RecommendationDTO response = recommendationService.recommend(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
