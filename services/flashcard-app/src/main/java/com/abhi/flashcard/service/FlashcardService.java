package com.abhi.flashcard.service;

import com.abhi.flashcard.dto.request.FlashcardRequest;
import com.abhi.flashcard.dto.request.ReorderRequest;
import com.abhi.flashcard.dto.response.FlashcardResponse;

import java.util.List;

public interface FlashcardService {

    List<FlashcardResponse> getFlashcardsByDeck(String deckRefId);

    FlashcardResponse getFlashcardById(String refId);

    FlashcardResponse createFlashcard(String deckRefId, FlashcardRequest request);

    FlashcardResponse updateFlashcard(String refId, FlashcardRequest request);

    void deleteFlashcard(String refId);

    void reorderFlashcards(String deckRefId, ReorderRequest request);
}
