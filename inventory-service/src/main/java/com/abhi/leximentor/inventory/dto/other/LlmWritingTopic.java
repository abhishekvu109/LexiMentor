package com.abhi.leximentor.inventory.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class LlmWritingTopic {
    @JsonProperty(value = "topicNo")
    private int topicNo;
    @JsonProperty(value = "topic")
    private String topic;
    @JsonProperty(value = "subject")
    private String subject;
    @JsonProperty(value = "description")
    private List<String> descriptions;
    @JsonProperty(value = "points")
    private List<String> points;
    @JsonProperty(value = "learning")
    private String learning;
}
