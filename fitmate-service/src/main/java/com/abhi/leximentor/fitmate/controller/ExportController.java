package com.abhi.leximentor.fitmate.controller;

import com.abhi.leximentor.fitmate.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
            @PathVariable String format) {

        StreamingResponseBody responseBody;
        String filename = type + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        if ("exercises".equalsIgnoreCase(type)) {
            responseBody = exportService.exportExercises(format);
        } else if ("routines".equalsIgnoreCase(type)) {
            responseBody = exportService.exportRoutines(format);
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
