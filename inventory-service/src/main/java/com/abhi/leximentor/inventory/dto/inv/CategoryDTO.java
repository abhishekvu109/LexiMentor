package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class CategoryDTO {
    private String refId;
    private String category;
    private String source;

}
