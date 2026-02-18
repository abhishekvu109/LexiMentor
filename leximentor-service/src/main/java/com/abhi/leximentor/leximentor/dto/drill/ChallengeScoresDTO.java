package com.abhi.leximentor.leximentor.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeScoresDTO {
    private String refId;
    private String drillChallengeRefId;
    private String drillSetRefId;
    private boolean isCorrect;
    private String response;
    private String question;
    private LocalDateTime crtnDate;
    private String description;
}
