package com.abhi.flashcard.dto.request;

import com.abhi.flashcard.entity.enums.DeckStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeckRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String categoryRefId;

    private DeckStatus status = DeckStatus.ACTIVE;

    private boolean isPublic = false;
}
