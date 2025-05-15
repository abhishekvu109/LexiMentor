package com.abhi.writewise.inventory.dto.response;

import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationDTO;
import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    private long refId;
    private TopicDTO topic;
    private String response;
    private String responseStatus;
    private EvaluationDTO evaluation;
}
