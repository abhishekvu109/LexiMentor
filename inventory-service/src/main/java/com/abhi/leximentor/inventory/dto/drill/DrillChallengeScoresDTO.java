package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillChallengeScoresDTO {
    private String refId;
    private long drillChallengeId;
    private long drillSetId;
    private boolean isCorrect;
    private String response;
    private LocalDateTime crtnDate;
    private String description;
}
