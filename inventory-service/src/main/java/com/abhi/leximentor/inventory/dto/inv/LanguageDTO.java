package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class LanguageDTO {
    private long refId;
    private String language;
    private String status;
}
