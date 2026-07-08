package com.abhi.flashcard.dto.response;

import com.abhi.flashcard.entity.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentBlockResponse {

    private String refId;
    private ContentType contentType;
    private String content;
    private String metadata;
    private Integer orderIndex;
}
