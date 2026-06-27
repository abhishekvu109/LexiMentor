package com.abhi.leximentor.leximentor.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillSetDTO {
    private String key;
    private String drillKey;
    private LocalDateTime createdAt;
    private String wordKey;
    private String word;
}
