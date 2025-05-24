package com.abhi.writewise.inventory.entities.nosql.mongodb.response;

import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseVersion {
    private long refId;
    private String uuid;
    private int versionNumber;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdDate;
    private boolean isLatest;
    private int status;
    private String response;
    private int responseStatus;
    private Evaluation evaluation;
}
