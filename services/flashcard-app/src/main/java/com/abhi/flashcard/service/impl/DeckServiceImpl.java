package com.abhi.flashcard.service.impl;

import com.abhi.flashcard.dto.request.DeckRequest;
import com.abhi.flashcard.dto.response.ContentBlockResponse;
import com.abhi.flashcard.dto.response.DeckResponse;
import com.abhi.flashcard.dto.response.DeckSummaryResponse;
import com.abhi.flashcard.dto.response.FlashcardResponse;
import com.abhi.flashcard.entity.Category;
import com.abhi.flashcard.entity.Deck;
import com.abhi.flashcard.entity.enums.DeckStatus;
import com.abhi.flashcard.exception.ResourceNotFoundException;
import com.abhi.flashcard.repository.CategoryRepository;
import com.abhi.flashcard.repository.DeckRepository;
import com.abhi.flashcard.repository.FlashcardRepository;
import com.abhi.flashcard.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final CategoryRepository categoryRepository;
    private final FlashcardRepository flashcardRepository;

    @Override
    public Page<DeckSummaryResponse> getDecksByCategory(String categoryRefId, Pageable pageable) {
        categoryRepository.findByRefId(categoryRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryRefId));
        return deckRepository.findByCategoryRefId(categoryRefId, pageable).map(this::toSummaryResponse);
    }

    @Override
    public Page<DeckSummaryResponse> getDecksByUser(String userId, Pageable pageable) {
        return deckRepository.findByUserId(userId, pageable).map(this::toSummaryResponse);
    }

    @Override
    public Page<DeckSummaryResponse> getPublicDecks(Pageable pageable) {
        return deckRepository.findByIsPublicTrue(pageable).map(this::toSummaryResponse);
    }

    @Override
    public DeckResponse getDeckById(String refId) {
        return toResponse(deckRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", refId)));
    }

    @Override
    @Transactional
    public DeckResponse createDeck(DeckRequest request) {
        Category category = resolveCategory(request.getCategoryRefId());
        Deck deck = Deck.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .userId(request.getUserId())
                .category(category)
                .status(request.getStatus() != null ? request.getStatus() : DeckStatus.ACTIVE)
                .isPublic(request.isPublic())
                .build();
        return toResponse(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public DeckResponse updateDeck(String refId, DeckRequest request) {
        Deck deck = deckRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", refId));
        deck.setTitle(request.getTitle());
        deck.setDescription(request.getDescription());
        deck.setCategory(resolveCategory(request.getCategoryRefId()));
        if (request.getStatus() != null) {
            deck.setStatus(request.getStatus());
        }
        deck.setPublic(request.isPublic());
        return toResponse(deckRepository.save(deck));
    }

    @Override
    @Transactional
    public void deleteDeck(String refId) {
        Deck deck = deckRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", refId));
        deckRepository.delete(deck);
    }

    private Category resolveCategory(String categoryRefId) {
        if (categoryRefId == null) return null;
        return categoryRepository.findByRefId(categoryRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryRefId));
    }

    private DeckSummaryResponse toSummaryResponse(Deck deck) {
        return DeckSummaryResponse.builder()
                .refId(deck.getRefId())
                .title(deck.getTitle())
                .description(deck.getDescription())
                .userId(deck.getUserId())
                .categoryRefId(deck.getCategory() != null ? deck.getCategory().getRefId() : null)
                .categoryName(deck.getCategory() != null ? deck.getCategory().getName() : null)
                .status(deck.getStatus())
                .isPublic(deck.isPublic())
                .flashcardCount(flashcardRepository.countByDeckRefId(deck.getRefId()))
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }

    private DeckResponse toResponse(Deck deck) {
        return DeckResponse.builder()
                .refId(deck.getRefId())
                .title(deck.getTitle())
                .description(deck.getDescription())
                .userId(deck.getUserId())
                .categoryRefId(deck.getCategory() != null ? deck.getCategory().getRefId() : null)
                .categoryName(deck.getCategory() != null ? deck.getCategory().getName() : null)
                .status(deck.getStatus())
                .isPublic(deck.isPublic())
                .flashcards(deck.getFlashcards().stream()
                        .map(f -> FlashcardResponse.builder()
                                .refId(f.getRefId())
                                .deckRefId(deck.getRefId())
                                .question(f.getQuestion())
                                .orderIndex(f.getOrderIndex())
                                .tags(f.getTags())
                                .answerBlocks(f.getAnswerBlocks().stream()
                                        .map(b -> ContentBlockResponse.builder()
                                                .refId(b.getRefId())
                                                .contentType(b.getContentType())
                                                .content(b.getContent())
                                                .metadata(b.getMetadata())
                                                .orderIndex(b.getOrderIndex())
                                                .build())
                                        .toList())
                                .createdAt(f.getCreatedAt())
                                .updatedAt(f.getUpdatedAt())
                                .build())
                        .toList())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }
}
