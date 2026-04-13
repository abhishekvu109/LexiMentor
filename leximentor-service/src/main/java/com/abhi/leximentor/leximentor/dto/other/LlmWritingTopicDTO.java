package com.abhi.leximentor.leximentor.dto.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmWritingTopicDTO {
    private String subject;
    private int numOfTopic;
    private String exam;
    private String prompt;
    private String response;
    private List<String> topics;
}
