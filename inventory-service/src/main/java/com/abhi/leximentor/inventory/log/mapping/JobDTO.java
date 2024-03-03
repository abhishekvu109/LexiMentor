package com.abhi.leximentor.inventory.log.mapping;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@ToString
@Slf4j
@Data
@Builder
public class JobDTO {
    private long jobId;
    private String refId;
    private LocalDateTime crtnDate;
    private int status;
}
