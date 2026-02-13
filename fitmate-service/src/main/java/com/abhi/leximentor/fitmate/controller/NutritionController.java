package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.constants.ApplicationConstants;
import com.abhi.leximentor.fitmate.model.ResponseEntityBuilder;
import com.abhi.leximentor.fitmate.model.RestApiResponse;
import com.abhi.leximentor.fitmate.service.NutritionService;
import com.abhi.leximentor.fitmate.dto.FoodEntryDTO;
import com.abhi.leximentor.fitmate.dto.NutritionDailySummaryDTO;
import com.abhi.leximentor.fitmate.dto.NutritionGoalDTO;
import com.abhi.leximentor.fitmate.dto.NutritionTrendSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fitmate/nutrition")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NutritionController {
    private final NutritionService nutritionService;

    @PostMapping(value = "/goals", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> upsertGoal(@RequestBody NutritionGoalDTO request) {
        try {
            NutritionGoalDTO response = nutritionService.upsertGoal(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to upsert nutrition goal", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @GetMapping(value = "/goals", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getGoal(@RequestParam String username) {
        try {
            NutritionGoalDTO response = nutritionService.getActiveGoal(username);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to fetch nutrition goal", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @PostMapping(value = "/entries", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addEntry(@RequestBody FoodEntryDTO request) {
        try {
            FoodEntryDTO response = nutritionService.addEntry(request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to add food entry", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @PutMapping(value = "/entries/{refId}", consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> updateEntry(@PathVariable long refId,
                                                                     @RequestParam String username,
                                                                     @RequestBody FoodEntryDTO request) {
        try {
            FoodEntryDTO response = nutritionService.updateEntry(username, refId, request);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to update food entry", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @DeleteMapping(value = "/entries/{refId}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteEntry(@PathVariable long refId,
                                                                     @RequestParam String username) {
        try {
            nutritionService.deleteEntry(username, refId);
            return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Deleted successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to delete food entry", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @GetMapping(value = "/entries", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getEntries(@RequestParam String username,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                    @RequestParam(required = false) String mealType) {
        try {
            List<FoodEntryDTO> response = nutritionService.getEntries(username, fromDate, toDate, mealType);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to fetch food entries", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @GetMapping(value = "/summary/daily", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getDailySummary(@RequestParam String username,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            NutritionDailySummaryDTO response = nutritionService.getDailySummary(username, date);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to fetch daily nutrition summary", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }

    @GetMapping(value = "/summary/trends", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getTrends(@RequestParam String username,
                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            NutritionTrendSummaryDTO response = nutritionService.getTrends(username, fromDate, toDate);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                    .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to fetch nutrition trends", ex);
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server exception");
        }
    }
}
