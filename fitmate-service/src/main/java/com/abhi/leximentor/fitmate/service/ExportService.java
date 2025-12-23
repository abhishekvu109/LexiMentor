package com.abhi.leximentor.fitmate.service;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface ExportService {
    StreamingResponseBody exportExercises(String format);

    StreamingResponseBody exportRoutines(String format);
}
