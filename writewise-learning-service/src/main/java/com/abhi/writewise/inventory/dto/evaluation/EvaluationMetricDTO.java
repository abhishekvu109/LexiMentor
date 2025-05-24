package com.abhi.writewise.inventory.dto.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationMetricDTO {
    private String refId;
    private String uuid;
    private String category;
    private double score;
    private List<String> comments;
    private List<String> alternateSuggestions;
}
