package com.abhi.leximentor.inventory.dto.drill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillSetDTO {
    private String refId;
    private long drillId;
    private LocalDateTime crtnDate;
    private long wordId;
    private String word;
}
