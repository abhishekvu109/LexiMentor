package com.abhi.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private String refId;
    private String name;
    private String description;
    private String parentRefId;
    private String parentName;
    private List<CategorySummaryResponse> subCategories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
