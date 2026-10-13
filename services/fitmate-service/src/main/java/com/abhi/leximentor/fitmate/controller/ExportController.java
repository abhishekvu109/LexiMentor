package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/fitmate/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/{type}")
    public ResponseEntity<StreamingResponseBody> export(
            @PathVariable String type,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "trainingRefId", required = false) String trainingRefId,
            @RequestParam(value = "bodyPartRefId", required = false) String bodyPartRefId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "mealType", required = false) String mealType) {

        StreamingResponseBody responseBody;
        String filename;

        if (StringUtils.equalsIgnoreCase("exercises", type)) {
            filename = "exercises_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            responseBody = exportService.exportExercises(trainingRefId, bodyPartRefId);
        } else if (StringUtils.equalsIgnoreCase("routines", type)) {
            if (isUserDateRangeInvalid(username, fromDate, toDate)) {
                return ResponseEntity.badRequest().build();
            }
            filename = "routines_" + username + "_" + fromDate + "_to_" + toDate;
            responseBody = exportService.exportRoutines(username.trim(), fromDate, toDate, status);
        } else if (StringUtils.equalsIgnoreCase("drills", type)) {
            if (isUserDateRangeInvalid(username, fromDate, toDate)) {
                return ResponseEntity.badRequest().build();
            }
            filename = "drills_" + username + "_" + fromDate + "_to_" + toDate;
            responseBody = exportService.exportRoutineDrills(username.trim(), fromDate, toDate);
        } else if (StringUtils.equalsIgnoreCase("nutrition", type)) {
            if (isUserDateRangeInvalid(username, fromDate, toDate)) {
                return ResponseEntity.badRequest().build();
            }
            filename = "nutrition_" + username + "_" + fromDate + "_to_" + toDate;
            responseBody = exportService.exportNutrition(username.trim(), fromDate, toDate, mealType);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(responseBody);
    }

    private boolean isUserDateRangeInvalid(String username, LocalDate fromDate, LocalDate toDate) {
        return username == null || username.isBlank()
                || fromDate == null || toDate == null
                || fromDate.isAfter(toDate);
    }
}
