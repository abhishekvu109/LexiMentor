package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillSetDTO {
    private String refId;
    private String drillRefId;
    private LocalDateTime crtnDate;
    private String wordRefId;
    private String word;
}
