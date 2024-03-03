package com.abhi.leximentor.inventory.log.mapping;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@Builder

public class WordRecord {
    private long id;
    private String refId;
    private String word;
    private String status;
    private LocalDateTime loadDate;
    private long jobId;
}
