package com.abhi.saarthi.apigateway.dto;

import java.util.List;

public record IntrospectionResponse(
        Boolean active,
        String sub,
        List<String> scope,
        Long exp,
        String error,
        String status
) {
}
