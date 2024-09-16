package com.abhi.leximentor.ai.rest;

import com.abhi.leximentor.ai.constants.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestApiResponse {
    private Meta meta;
    private Object data;
    private List<CustomError> errors;

    public static RestApiResponse buildSuccessResponse(String code, String message, Object result) {
        RestApiResponse response = new RestApiResponse();
        Meta meta = new Meta();
        meta.setCode(code);
        meta.setDescription(message);
        meta.setStatus(ApplicationConstants.STATUS_SUCCESS);
        response.setMeta(meta);
        response.setData(result);
        return response;
    }

    public static RestApiResponse buildFailureResponse(String code, String message) {
        RestApiResponse response = new RestApiResponse();
        Meta meta = new Meta();
        meta.setCode(code);
        meta.setDescription(message);
        meta.setStatus(ApplicationConstants.STATUS_FAILURE);
        response.setMeta(meta);
        return response;
    }
}
