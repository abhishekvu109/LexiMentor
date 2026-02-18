package com.abhi.leximentor.leximentor.dto.inv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class AntonymDTO {
    private String refId;
    private String wordRefId;
    private String word;
    private String antonym;
    private String status;
    private String source;

}
