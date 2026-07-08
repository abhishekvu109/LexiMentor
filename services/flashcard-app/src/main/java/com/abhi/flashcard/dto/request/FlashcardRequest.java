package com.abhi.flashcard.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlashcardRequest {

    @NotBlank(message = "Question is required")
    private String question;

    @Valid
    private List<ContentBlockRequest> answerBlocks = new ArrayList<>();

    private Integer orderIndex;

    private List<String> tags = new ArrayList<>();
}
