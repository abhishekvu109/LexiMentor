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
public class InsightDTO {
    private String insight;
    private LocalDateTime generatedAt;
}
