package com.abhi.leximentor.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@Data
@ToString
@AllArgsConstructor
public class WordDifficultyDTO {
    private long wordRefId;
    private String word;
    private long wrongCount;
}
