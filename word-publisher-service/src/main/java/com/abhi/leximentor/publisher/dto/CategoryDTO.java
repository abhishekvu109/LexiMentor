package com.abhi.leximentor.publisher.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class CategoryDTO {
    private String key;
    private String category;
}
