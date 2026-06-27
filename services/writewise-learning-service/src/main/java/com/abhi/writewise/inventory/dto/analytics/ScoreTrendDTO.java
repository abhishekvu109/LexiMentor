package com.abhi.writewise.inventory.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreTrendDTO {
    private int versionNumber;
    private double score;
    private String topicName;
    private LocalDateTime date;
}
