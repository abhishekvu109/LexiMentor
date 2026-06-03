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
public class CreateTopicManuallyDTO {
    @JsonProperty(value = "topic")
    private String topic;

    @JsonProperty(value = "subject")
    private String subject;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "points")
    private List<String> points;

    @JsonProperty(value = "learning")
    private String learning;

    @JsonProperty("recommendations")
    private List<String> recommendations;

    @JsonProperty(value = "wordCount")
    private int wordCount;
}
