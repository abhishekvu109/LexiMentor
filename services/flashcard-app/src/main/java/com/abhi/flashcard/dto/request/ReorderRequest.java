package com.abhi.flashcard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReorderRequest {

    @NotNull(message = "Ordered flashcard refIds are required")
    private List<String> orderedRefIds;
}
