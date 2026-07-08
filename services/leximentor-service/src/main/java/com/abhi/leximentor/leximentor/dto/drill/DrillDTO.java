package com.abhi.leximentor.leximentor.dto.drill;

import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrillDTO {
    private String key;
    private String name;
    private String drillName;
    private String status;
    private LocalDateTime createdAt;
    private double overAllScore;
    private List<DrillSetDTO> drillSetDTOList;
    private List<ChallengeDTO> ChallengeDTOList;
    private NamedObjectDTO namedObjectDTO;
}
