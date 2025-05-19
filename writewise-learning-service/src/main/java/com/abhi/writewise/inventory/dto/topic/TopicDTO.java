package com.abhi.writewise.inventory.dto.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicDTO {
    @JsonProperty(value = "topicNo")
    private int topicNo;
    @JsonProperty(value = "topic")
    private String topic;
    @JsonProperty(value = "subject")
    private String subject;
    @JsonProperty(value = "description")
    private String descriptions;
    @JsonProperty(value = "points")
    private List<String> points;
    @JsonProperty(value = "learning")
    private String learning;
    @JsonProperty(value = "refId")
    private String refId;
    @JsonProperty(value = "uuid")
    private String uuid;
    @JsonProperty("recommendations")
    private List<String> recommendations;
    @JsonProperty("writingSessionRefId")
    private String writingSessionRefId;
}
