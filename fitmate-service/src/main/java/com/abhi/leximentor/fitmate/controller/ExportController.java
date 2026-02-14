package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/fitmate/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/{type}/{format}")
    public ResponseEntity<StreamingResponseBody> export(
            @PathVariable String type,
            @PathVariable String format,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        StreamingResponseBody responseBody;
        if (!"csv".equalsIgnoreCase(format) && !"json".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().build();
        }

        String filename = type + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        if (StringUtils.equalsIgnoreCase("exercises",type)) {
            responseBody = exportService.exportExercises(format);
        } else if (StringUtils.equalsIgnoreCase("routines",type)) {
            responseBody = exportService.exportRoutines(format);
        } else if (StringUtils.equalsIgnoreCase("drills",type)) {
            if (username == null || username.isBlank() || fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
                return ResponseEntity.badRequest().build();
            }
            filename = type + "_" + username + "_" + fromDate + "_to_" + toDate;
            responseBody = exportService.exportRoutineDrills(format, username.trim(), fromDate, toDate);
        } else if (StringUtils.equalsIgnoreCase("nutrition",type)) {
            if (username == null || username.isBlank() || fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
                return ResponseEntity.badRequest().build();
            }
            filename = type + "_" + username + "_" + fromDate + "_to_" + toDate;
            responseBody = exportService.exportNutrition(format, username.trim(), fromDate, toDate);
        } else {
            return ResponseEntity.badRequest().build();
        }

        String extension = "csv".equalsIgnoreCase(format) ? ".csv" : ".json";
        MediaType mediaType = "csv".equalsIgnoreCase(format) ? MediaType.parseMediaType("text/csv")
                : MediaType.APPLICATION_JSON;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + extension)
                .contentType(mediaType)
                .body(responseBody);
    }
}
