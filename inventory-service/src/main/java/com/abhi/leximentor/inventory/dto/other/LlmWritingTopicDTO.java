package com.abhi.leximentor.inventory.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LlmWritingTopicDTO {
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("numOfTopic")
    private int numOfTopic;
    @JsonProperty(value = "exam", defaultValue = "IELTS")
    private String exam;
    private String prompt;
    private List<LlmWritingTopic> topics;
    private List<String> recommendations;
}
