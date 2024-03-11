package com.abhi.leximentor.inventory.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class LanguageDTO {
    private String refId;
    private String language;
    private String status;
}
