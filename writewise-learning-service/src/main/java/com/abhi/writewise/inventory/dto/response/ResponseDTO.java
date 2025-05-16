package com.abhi.writewise.inventory.dto.response;

import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    private String refId;
    private String uuid;
    private TopicDTO topic;
    private List<ResponseVersionDTO> responseVersionDTOs;
}
