package com.abhi.flashcard.controller;

import com.abhi.flashcard.dto.request.DeckRequest;
import com.abhi.flashcard.dto.response.ApiResponse;
import com.abhi.flashcard.dto.response.DeckResponse;
import com.abhi.flashcard.dto.response.DeckSummaryResponse;
import com.abhi.flashcard.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flashcard/v1/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DeckSummaryResponse>>> getDecks(
            @RequestParam(required = false) String categoryRefId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<DeckSummaryResponse> result;
        if (categoryRefId != null) {
            result = deckService.getDecksByCategory(categoryRefId, pageable);
        } else if (userId != null) {
            result = deckService.getDecksByUser(userId, pageable);
        } else {
            result = deckService.getPublicDecks(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse<DeckResponse>> getDeckById(@PathVariable String refId) {
        return ResponseEntity.ok(ApiResponse.success(deckService.getDeckById(refId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeckResponse>> createDeck(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody DeckRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deck created successfully", deckService.createDeck(request, userId)));
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse<DeckResponse>> updateDeck(
            @PathVariable String refId,
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody DeckRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Deck updated successfully", deckService.updateDeck(refId, request, userId)));
    }

    @DeleteMapping("/{refId}")
    public ResponseEntity<ApiResponse<Void>> deleteDeck(@PathVariable String refId) {
        deckService.deleteDeck(refId);
        return ResponseEntity.ok(ApiResponse.success("Deck deleted successfully", null));
    }
}
