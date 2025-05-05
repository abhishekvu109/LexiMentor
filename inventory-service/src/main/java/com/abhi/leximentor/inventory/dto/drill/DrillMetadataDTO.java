package com.abhi.leximentor.inventory.dto.drill;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillMetadataDTO {
    private String refId;
    private String name;
    private String drillName;
    private String status;
    private LocalDateTime crtnDate;
    private double overAllScore;
    private List<DrillSetDTO> drillSetDTOList;
    private List<DrillChallengeDTO> drillChallengeDTOList;
}
