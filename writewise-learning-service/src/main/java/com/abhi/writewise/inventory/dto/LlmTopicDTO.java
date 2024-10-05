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
    @JsonProperty(value = "purpose", defaultValue = "IELTS exam")
    private String purpose;
    @JsonProperty(value = "wordCount", defaultValue = "100")
    private Integer wordCount;
    private String prompt;
    private List<TopicDTO> topics;
    private List<String> recommendations;
}
