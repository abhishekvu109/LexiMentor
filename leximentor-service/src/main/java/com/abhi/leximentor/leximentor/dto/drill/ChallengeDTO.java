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
    private String refId;
    private String drillType;
    private String drillRefId;
    private double drillScore;
    private boolean isPass;
    private int totalCorrect;
    private int totalWrong;
    private LocalDateTime crtnDate;
    private List<ChallengeScoresDTO> ChallengeScoresDTOList;
    private String status;
    private String evaluationStatus;
    private String username;
}
