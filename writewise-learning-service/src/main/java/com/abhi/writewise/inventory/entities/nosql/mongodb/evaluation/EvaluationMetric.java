package com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationMetric {

    private long refId;
    private String uuid;
    private int category;
    private double score; // e.g., 0 to 10
    private List<String> comments; // model feedback
    private List<String> alternateSuggestions; // optional alternate way of writing

}
