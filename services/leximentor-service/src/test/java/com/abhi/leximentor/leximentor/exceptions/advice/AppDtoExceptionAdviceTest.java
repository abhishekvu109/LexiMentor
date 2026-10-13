package com.abhi.leximentor.leximentor.exceptions.advice;

import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AppDtoExceptionAdviceTest {

    private final AppDtoExceptionAdvice advice = new AppDtoExceptionAdvice();

    @Test
    void duplicateObjectFound_shouldReturnConflict() {
        ResponseEntity<RestApiResponse> response = advice.duplicateObjectFound(new ServerException().new DuplicateEntityObjectFound("duplicate"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getMeta().getStatus());
    }

    @Test
    void internalErrorException_shouldReturnInternalServerError() {
        ResponseEntity<RestApiResponse> response = advice.internalErrorException(new ServerException().new InternalError("internal"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getMeta().getStatus());
    }

    @Test
    void unknownException_shouldReturnInternalServerError() {
        ResponseEntity<RestApiResponse> response = advice.unknownException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getMeta().getStatus());
    }
}
