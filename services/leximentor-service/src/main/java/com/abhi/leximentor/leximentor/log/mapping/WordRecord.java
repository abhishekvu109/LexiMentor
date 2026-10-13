package com.abhi.leximentor.leximentor.log.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordRecord {
    private long id;
    private String refId;
    private String word;
    private int status;
    private LocalDateTime loadDate;
    private long jobId;
}
