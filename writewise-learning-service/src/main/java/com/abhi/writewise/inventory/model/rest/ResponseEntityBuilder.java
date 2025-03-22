package com.abhi.writewise.inventory.model.rest;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseEntityBuilder {
    private final HttpStatus httpStatus;

    public static ResponseEntityBuilder getBuilder(HttpStatus httpStatus) {
        return new ResponseEntityBuilder(httpStatus);
    }

    public ResponseEntity<RestApiResponse> successResponse(String message, Object result) {
        return successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, message, result);
    }

    public ResponseEntity<RestApiResponse> successResponse(String code, String message, Object result) {
        RestApiResponse response = RestApiResponse.buildSuccessResponse(code, message, result);
        return new ResponseEntity<>(response, httpStatus);
    }

    public ResponseEntity<RestApiResponse> errorResponse(String code, String message) {
        RestApiResponse response = RestApiResponse.buildFailureResponse(code, message);
        return new ResponseEntity<>(response, httpStatus);
    }
}
