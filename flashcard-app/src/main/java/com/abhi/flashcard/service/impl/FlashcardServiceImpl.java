package com.abhi.flashcard.service.impl;

import com.abhi.flashcard.dto.request.ContentBlockRequest;
import com.abhi.flashcard.dto.request.FlashcardRequest;
import com.abhi.flashcard.dto.request.ReorderRequest;
import com.abhi.flashcard.dto.response.ContentBlockResponse;
import com.abhi.flashcard.dto.response.FlashcardResponse;
import com.abhi.flashcard.entity.ContentBlock;
import com.abhi.flashcard.entity.Deck;
import com.abhi.flashcard.entity.Flashcard;
import com.abhi.flashcard.exception.BadRequestException;
import com.abhi.flashcard.exception.ResourceNotFoundException;
import com.abhi.flashcard.repository.DeckRepository;
import com.abhi.flashcard.repository.FlashcardRepository;
import com.abhi.flashcard.service.FlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;

    @Override
    public List<FlashcardResponse> getFlashcardsByDeck(String deckRefId) {
        deckRepository.findByRefId(deckRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", deckRefId));
        return flashcardRepository.findByDeckRefIdOrderByOrderIndexAsc(deckRefId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FlashcardResponse getFlashcardById(String refId) {
        return toResponse(flashcardRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", refId)));
    }

    @Override
    @Transactional
    public FlashcardResponse createFlashcard(String deckRefId, FlashcardRequest request, String userId) {
        Deck deck = deckRepository.findByRefId(deckRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", deckRefId));
        long count = flashcardRepository.countByDeckRefId(deckRefId);
        Flashcard flashcard = Flashcard.builder()
                .deck(deck)
                .question(request.getQuestion())
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : (int) count)
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .build();
        flashcard.setUserId(userId);
        flashcard.setAnswerBlocks(buildContentBlocks(request.getAnswerBlocks(), flashcard));
        return toResponse(flashcardRepository.save(flashcard));
    }

    @Override
    @Transactional
    public FlashcardResponse updateFlashcard(String refId, FlashcardRequest request, String userId) {
        Flashcard flashcard = flashcardRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", refId));
        flashcard.setQuestion(request.getQuestion());
        flashcard.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        if (request.getOrderIndex() != null) {
            flashcard.setOrderIndex(request.getOrderIndex());
        }
        flashcard.getAnswerBlocks().clear();
        flashcard.getAnswerBlocks().addAll(buildContentBlocks(request.getAnswerBlocks(), flashcard));
        return toResponse(flashcardRepository.save(flashcard));
    }

    @Override
    @Transactional
    public void deleteFlashcard(String refId) {
        Flashcard flashcard = flashcardRepository.findByRefId(refId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard", refId));
        flashcardRepository.delete(flashcard);
    }

    @Override
    @Transactional
    public void reorderFlashcards(String deckRefId, ReorderRequest request) {
        deckRepository.findByRefId(deckRefId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", deckRefId));
        AtomicInteger index = new AtomicInteger(0);
        request.getOrderedRefIds().forEach(flashcardRefId -> {
            Flashcard flashcard = flashcardRepository.findByRefId(flashcardRefId)
                    .orElseThrow(() -> new ResourceNotFoundException("Flashcard", flashcardRefId));
            if (!flashcard.getDeck().getRefId().equals(deckRefId)) {
                throw new BadRequestException("Flashcard " + flashcardRefId + " does not belong to deck " + deckRefId);
            }
            flashcard.setOrderIndex(index.getAndIncrement());
            flashcardRepository.save(flashcard);
        });
    }

    private List<ContentBlock> buildContentBlocks(List<ContentBlockRequest> requests, Flashcard flashcard) {
        if (requests == null || requests.isEmpty()) return new ArrayList<>();
        AtomicInteger idx = new AtomicInteger(0);
        return requests.stream()
                .map(req -> ContentBlock.builder()
                        .flashcard(flashcard)
                        .contentType(req.getContentType())
                        .content(req.getContent())
                        .metadata(req.getMetadata())
                        .orderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : idx.getAndIncrement())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private FlashcardResponse toResponse(Flashcard f) {
        return FlashcardResponse.builder()
                .refId(f.getRefId())
                .deckRefId(f.getDeck().getRefId())
                .question(f.getQuestion())
                .answerBlocks(f.getAnswerBlocks().stream()
                        .map(b -> ContentBlockResponse.builder()
                                .refId(b.getRefId())
                                .contentType(b.getContentType())
                                .content(b.getContent())
                                .metadata(b.getMetadata())
                                .orderIndex(b.getOrderIndex())
                                .build())
                        .toList())
                .orderIndex(f.getOrderIndex())
                .tags(f.getTags())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
