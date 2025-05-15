package com.abhi.writewise.inventory.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMasterDTO {
    private String topicGenerationRefId;
    private String refId;
    private String uuid;
    private String status;
    private double score;
    private boolean isPassed;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private List<ResponseDTO> topicResponseList;
}
