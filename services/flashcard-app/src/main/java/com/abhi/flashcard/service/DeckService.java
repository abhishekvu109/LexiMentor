package com.abhi.flashcard.service;

import com.abhi.flashcard.dto.request.DeckRequest;
import com.abhi.flashcard.dto.response.DeckResponse;
import com.abhi.flashcard.dto.response.DeckSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeckService {

    Page<DeckSummaryResponse> getDecksByCategory(String categoryRefId, Pageable pageable);

    Page<DeckSummaryResponse> getDecksByUser(String userId, Pageable pageable);

    Page<DeckSummaryResponse> getPublicDecks(Pageable pageable);

    DeckResponse getDeckById(String refId);

    DeckResponse createDeck(DeckRequest request);

    DeckResponse updateDeck(String refId, DeckRequest request);

    void deleteDeck(String refId);
}
