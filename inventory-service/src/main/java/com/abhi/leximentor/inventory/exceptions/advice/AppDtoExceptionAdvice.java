package com.abhi.leximentor.inventory.exceptions.advice;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.exceptions.entities.InvalidDTOException;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class AppDtoExceptionAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleDtoValidaton(MethodArgumentNotValidException ex) {
        Map<String, String> errorList = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorList.put(error.getField(), error.getDefaultMessage());
        });
        return errorList;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ServerException.DuplicateEntityObjectFound.class)
    public ResponseEntity<RestApiResponse> duplicateObjectFound(ServerException.DuplicateEntityObjectFound ex) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.CONFLICT).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, String.format("The object already exist in our database. %s", ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ServerException.EntityObjectNotFound.class)
    public ResponseEntity<RestApiResponse> objectNotFound(ServerException.EntityObjectNotFound ex) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.NOT_FOUND).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, String.format("The object doesn't exist in our database. %s", ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDTOException.class)
    public ResponseEntity<RestApiResponse> invalidRequest(InvalidDTOException ex) {
        return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, String.format("The request is invalid or some part of the input is incorrect. %s", ex.getMessage()));
    }
}
