package com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation;

import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationMetric {

    @Builder.Default
    private long refId = KeyGeneratorUtil.refId();
    @Builder.Default
    private String uuid = KeyGeneratorUtil.uuid();
    private int category;
    private double score; // e.g., 0 to 10
    private List<String> comments; // model feedback
    @JsonAlias("alternateSuggestions")
    private List<String> alternateSuggestion; // optional alternate way of writing

}
