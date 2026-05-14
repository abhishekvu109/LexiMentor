package com.abhi.flashcard.dto.request;

import com.abhi.flashcard.entity.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentBlockRequest {

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    @NotBlank(message = "Content is required")
    private String content;

    // Optional JSON string with type-specific metadata:
    //   IMAGE → {"mimeType":"image/png","altText":"diagram"}
    //   CODE  → {"language":"python","filename":"solution.py"}
    private String metadata;

    private Integer orderIndex;
}
