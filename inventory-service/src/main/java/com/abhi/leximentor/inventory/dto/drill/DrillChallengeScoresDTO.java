package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillChallengeScoresDTO {
    private long refId;
    private long drillChallengeRefId;
    private long drillSetRefId;
    private boolean isCorrect;
    private String response;
    private LocalDateTime crtnDate;
    private String description;
}
