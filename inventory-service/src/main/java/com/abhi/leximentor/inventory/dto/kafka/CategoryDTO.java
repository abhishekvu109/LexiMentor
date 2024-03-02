package com.abhi.leximentor.inventory.dto.kafka;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class CategoryDTO {
    private String key;
    private String category;
    private String source;

}
