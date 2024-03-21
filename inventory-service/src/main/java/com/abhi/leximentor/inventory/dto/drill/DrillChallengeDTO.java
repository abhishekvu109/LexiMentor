package com.abhi.leximentor.inventory.dto.drill;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillChallengeDTO {
    private String refId;
    private String drillType;
    private String drillRefId;
    private double drillScore;
    private boolean isPass;
    private int totalCorrect;
    private int totalWrong;
    private LocalDateTime crtnDate;
    private List<DrillChallengeScoresDTO> drillChallengeScoresDTOList;
    private String status;
}
