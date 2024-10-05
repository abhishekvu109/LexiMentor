package com.abhi.writewise.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class LlmTopicDTO {
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("numOfTopic")
    private int numOfTopic;
    @JsonProperty(value = "exam")
    private String exam;
    private String prompt;
    private List<TopicDTO> topics;
    private List<String> recommendations;
}
