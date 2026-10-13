package com.abhi.leximentor.leximentor.dto.drill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeScoresDTO {
    private String key;
    private String challengeKey;
    private String drillSetKey;
    private boolean isCorrect;
    private String response;
    private String question;
    private LocalDateTime createdAt;
    private String description;
}
