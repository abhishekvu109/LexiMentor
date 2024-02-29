package com.abhi.leximentor.publisher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class LanguageDTO {
    private String key;
    private String language;
    private String status;
}
