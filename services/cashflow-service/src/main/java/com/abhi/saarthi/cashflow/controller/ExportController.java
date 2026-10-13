package com.abhi.saarthi.cashflow.controller;

import com.abhi.saarthi.cashflow.model.ExportFilter;
import com.abhi.saarthi.cashflow.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/cashflow/export")
public class ExportController {

    private final ExportService exportService;

    @PostMapping(value = "/expenses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportExpenses(@RequestBody ExportFilter filter) {
        log.info("Export expenses requested with filter: {}", filter);
        byte[] csv = exportService.exportExpenses(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cashflow_expenses.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @PostMapping(value = "/earnings", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportEarnings(@RequestBody ExportFilter filter) {
        log.info("Export earnings requested with filter: {}", filter);
        byte[] csv = exportService.exportEarnings(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cashflow_earnings.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @PostMapping(value = "/deposits", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportDeposits(@RequestBody ExportFilter filter) {
        log.info("Export deposits requested with filter: {}", filter);
        byte[] csv = exportService.exportDeposits(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cashflow_deposits.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
