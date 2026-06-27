package com.abhi.leximentor.fitmate.service;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;

public interface ExportService {
    StreamingResponseBody exportExercises(String trainingRefId, String bodyPartRefId);

    StreamingResponseBody exportRoutines(String username, LocalDate fromDate, LocalDate toDate, String status);

    StreamingResponseBody exportRoutineDrills(String username, LocalDate fromDate, LocalDate toDate);

    StreamingResponseBody exportNutrition(String username, LocalDate fromDate, LocalDate toDate, String mealType);
}
