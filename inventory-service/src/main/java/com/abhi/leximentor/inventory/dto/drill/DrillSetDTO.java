package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillSetDTO {
    private long refId;
    private long drillRefId;
    private LocalDateTime crtnDate;
    private long wordId;
    private String word;
}
