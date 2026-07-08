package com.abhi.flashcard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class FlashcardResponse {

    private String refId;
    private String deckRefId;
    private String question;
    private List<ContentBlockResponse> answerBlocks;
    private Integer orderIndex;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
