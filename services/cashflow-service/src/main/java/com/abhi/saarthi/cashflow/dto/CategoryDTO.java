package com.abhi.saarthi.cashflow.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class CategoryDTO {
    private String uuid;
    private String refId;
    private String name;
    private String status;
}
