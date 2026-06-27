package com.abhi.leximentor.leximentor.dto.drill;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {
    private String key;
    private String challengeType;
    private String drillKey;
    private double drillScore;
    private boolean isPass;
    private int totalCorrect;
    private int totalWrong;
    private LocalDateTime createdAt;
    private List<ChallengeScoresDTO> challengeScoresDTOList;
    private String status;
    private String evaluationStatus;
    private String username;
}
