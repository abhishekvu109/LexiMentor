package com.abhi.flashcard.dto.response;

import com.abhi.flashcard.entity.enums.DeckStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeckSummaryResponse {

    private String refId;
    private String title;
    private String description;
    private String userId;
    private String categoryRefId;
    private String categoryName;
    private DeckStatus status;
    private boolean isPublic;
    private long flashcardCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
