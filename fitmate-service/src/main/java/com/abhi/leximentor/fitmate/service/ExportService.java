package com.abhi.leximentor.fitmate.service;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;

public interface ExportService {
    StreamingResponseBody exportExercises(String format);

    StreamingResponseBody exportRoutines(String format);

    StreamingResponseBody exportRoutineDrills(String format, String username, LocalDate fromDate, LocalDate toDate);
}
