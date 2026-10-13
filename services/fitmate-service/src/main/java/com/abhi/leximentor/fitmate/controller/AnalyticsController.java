package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@RestController
@Slf4j
@RequestMapping("/api/fitmate/analytics")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsController {

    private final AnalyticsService analyticsService;


    @GetMapping(value = "/overall", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getOverallAnalytics(@RequestParam("username") String username,
                                                               @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch overall analytics.", rangeDays,
                () -> analyticsService.getOverallAnalytics(username, rangeDays));
    }


    @GetMapping(value = "/exercise", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getExerciseAnalytics(@RequestParam("username") String username,
                                                                @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch exercise analytics.", rangeDays,
                () -> analyticsService.getExerciseAnalytics(username, rangeDays));
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDashboardSummary(@RequestParam("username") String username,
                                                               @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch dashboard summary.", rangeDays,
                () -> analyticsService.getDashboardSummary(username, rangeDays));
    }

    @GetMapping(value = "/workout-trends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWorkoutTrends(@RequestParam("username") String username,
                                                            @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch workout trends.", rangeDays,
                () -> analyticsService.getWorkoutTrends(username, rangeDays));
    }

    @GetMapping(value = "/body-part-workout-volume", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getBodyPartWorkoutVolume(@RequestParam("username") String username,
                                                                    @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch body part workout volume.", rangeDays,
                () -> analyticsService.getBodyPartWorkoutVolume(username, rangeDays));
    }

    @GetMapping(value = "/most-frequent-exercises", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getMostFrequentExercises(@RequestParam("username") String username,
                                                                    @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch most frequent exercises.", rangeDays,
                () -> analyticsService.getMostFrequentExercises(username, rangeDays));
    }

    @GetMapping(value = "/routine-distribution", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getRoutineDistributionByTrainingType(@RequestParam("username") String username,
                                                                                @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch routine distribution by training type.", rangeDays,
                () -> analyticsService.getRoutineDistributionByTrainingType(username, rangeDays));
    }

    @GetMapping(value = "/exercise-progressions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getExerciseProgressions(@RequestParam("username") String username,
                                                                   @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch exercise progressions.", rangeDays,
                () -> analyticsService.getExerciseProgressions(username, rangeDays));
    }

    @GetMapping(value = "/routine-efficiency", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getRoutineEfficiency(@RequestParam("username") String username,
                                                                @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch routine efficiency.", rangeDays,
                () -> analyticsService.getRoutineEfficiency(username, rangeDays));
    }

    @GetMapping(value = "/activity-consistency", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getActivityConsistency(@RequestParam("username") String username,
                                                                  @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch activity consistency.", rangeDays,
                () -> analyticsService.getActivityConsistency(username, rangeDays));
    }

    @GetMapping(value = "/calories-duration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getCaloriesDuration(@RequestParam("username") String username,
                                                               @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch calories and duration analytics.", rangeDays,
                () -> analyticsService.getCaloriesDuration(username, rangeDays));
    }

    @GetMapping(value = "/training-adherence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getTrainingAdherence(@RequestParam("username") String username,
                                                                @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch training adherence.", rangeDays,
                () -> analyticsService.getTrainingAdherence(username, rangeDays));
    }

    @GetMapping(value = "/exercise-performance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getExercisePerformance(@RequestParam("username") String username,
                                                                  @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch exercise performance.", rangeDays,
                () -> analyticsService.getExercisePerformance(username, rangeDays));
    }

    @GetMapping(value = "/muscle-body-part-focus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getMuscleBodyPartFocus(@RequestParam("username") String username,
                                                                  @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch muscle and body part focus.", rangeDays,
                () -> analyticsService.getMuscleBodyPartFocus(username, rangeDays));
    }

    @GetMapping(value = "/equipment-usage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getEquipmentUsage(@RequestParam("username") String username,
                                                             @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch equipment usage.", rangeDays,
                () -> analyticsService.getEquipmentUsage(username, rangeDays));
    }

    @GetMapping(value = "/difficulty-risk-mix", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDifficultyRiskMix(@RequestParam("username") String username,
                                                                @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch difficulty and risk mix.", rangeDays,
                () -> analyticsService.getDifficultyRiskMix(username, rangeDays));
    }

    @GetMapping(value = "/routine-structure", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getRoutineStructure(@RequestParam("username") String username,
                                                               @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch routine structure.", rangeDays,
                () -> analyticsService.getRoutineStructure(username, rangeDays));
    }

    @GetMapping(value = "/personal-records", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getPersonalRecords(@RequestParam("username") String username,
                                                              @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch personal records.", rangeDays,
                () -> analyticsService.getPersonalRecords(username, rangeDays));
    }

    @GetMapping(value = "/exercise-library-insights", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getExerciseLibraryInsights(@RequestParam("username") String username,
                                                                      @RequestParam(value = "rangeDays", required = false) Integer rangeDays) {
        return handleAnalytics("Failed to fetch exercise library insights.", rangeDays,
                () -> analyticsService.getExerciseLibraryInsights(username, rangeDays));
    }

    private ResponseEntity<RestApiResponse> handleAnalytics(String failureMessage,
                                                            Integer rangeDays,
                                                            Supplier<Object> supplier) {
        ResponseEntity<RestApiResponse> invalid = validateRangeDays(rangeDays);
        if (invalid != null) {
            return invalid;
        }
        try {
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, supplier.get());
        } catch (Exception e) {
            log.error(failureMessage, e);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, failureMessage);
        }
    }

    private ResponseEntity<RestApiResponse> validateRangeDays(Integer rangeDays) {
        if (rangeDays != null && rangeDays <= 0) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "rangeDays must be a positive number.");
        }
        return null;
    }
}
