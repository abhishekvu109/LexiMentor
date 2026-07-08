package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.dto.analytics.WritingAnalyticsDTO;
import com.abhi.writewise.inventory.dto.analytics.InsightDTO;
import com.abhi.writewise.inventory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/writewise/v1/analytics")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<WritingAnalyticsDTO> getInstantAnalytics() {
        log.info("Received request to fetch instant analytics");
        WritingAnalyticsDTO analytics = analyticsService.getInstantAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/insights")
    public ResponseEntity<InsightDTO> generateLlmInsights() {
        log.info("Received request to generate LLM insights");
        InsightDTO insight = analyticsService.generateLlmInsights();
        return ResponseEntity.ok(insight);
    }
}
