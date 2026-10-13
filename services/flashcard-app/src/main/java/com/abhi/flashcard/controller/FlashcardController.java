package com.abhi.flashcard.controller;

import com.abhi.flashcard.dto.request.FlashcardRequest;
import com.abhi.flashcard.dto.request.ReorderRequest;
import com.abhi.flashcard.dto.response.ApiResponse;
import com.abhi.flashcard.dto.response.FlashcardResponse;
import com.abhi.flashcard.service.FlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping("/api/flashcard/v1/decks/{deckRefId}/flashcards")
    public ResponseEntity<ApiResponse<List<FlashcardResponse>>> getFlashcardsByDeck(@PathVariable String deckRefId) {
        return ResponseEntity.ok(ApiResponse.success(flashcardService.getFlashcardsByDeck(deckRefId)));
    }

    @PostMapping("/api/flashcard/v1/decks/{deckRefId}/flashcards")
    public ResponseEntity<ApiResponse<FlashcardResponse>> createFlashcard(
            @PathVariable String deckRefId,
            @Valid @RequestBody FlashcardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flashcard created successfully",
                        flashcardService.createFlashcard(deckRefId, request)));
    }

    @PatchMapping("/api/flashcard/v1/decks/{deckRefId}/flashcards/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderFlashcards(
            @PathVariable String deckRefId,
            @Valid @RequestBody ReorderRequest request) {
        flashcardService.reorderFlashcards(deckRefId, request);
        return ResponseEntity.ok(ApiResponse.success("Flashcards reordered successfully", null));
    }

    @GetMapping("/api/flashcard/v1/flashcards/{refId}")
    public ResponseEntity<ApiResponse<FlashcardResponse>> getFlashcardById(@PathVariable String refId) {
        return ResponseEntity.ok(ApiResponse.success(flashcardService.getFlashcardById(refId)));
    }

    @PutMapping("/api/flashcard/v1/flashcards/{refId}")
    public ResponseEntity<ApiResponse<FlashcardResponse>> updateFlashcard(
            @PathVariable String refId,
            @Valid @RequestBody FlashcardRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Flashcard updated successfully",
                flashcardService.updateFlashcard(refId, request)));
    }

    @DeleteMapping("/api/flashcard/v1/flashcards/{refId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlashcard(@PathVariable String refId) {
        flashcardService.deleteFlashcard(refId);
        return ResponseEntity.ok(ApiResponse.success("Flashcard deleted successfully", null));
    }
}
