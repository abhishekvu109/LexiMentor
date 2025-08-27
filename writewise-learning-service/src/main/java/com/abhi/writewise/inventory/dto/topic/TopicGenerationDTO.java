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
public class TopicGenerationDTO {
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("numOfTopic")
    private int numOfTopic;
    @JsonProperty(value = "purpose", defaultValue = "IELTS exam")
    private String purpose;
    @JsonProperty(value = "wordCount", defaultValue = "100")
    private Integer wordCount;
    @JsonProperty("prompt")
    private String prompt;
    @JsonProperty("topics")
    private List<TopicDTO> topics;
    @JsonProperty("recommendations")
    private List<String> recommendations;
    @JsonProperty("status")
    private String status;
    @JsonProperty("refId")
    private String refId;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("writingSessionRefId")
    private String writingSessionRefId;
}
