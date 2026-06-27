package com.abhi.flashcard.repository;

import com.abhi.flashcard.entity.Deck;
import com.abhi.flashcard.entity.enums.DeckStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {

    Optional<Deck> findByRefId(String refId);

    Page<Deck> findByCategoryRefId(String categoryRefId, Pageable pageable);

    Page<Deck> findByUserId(String userId, Pageable pageable);

    Page<Deck> findByUserIdAndStatus(String userId, DeckStatus status, Pageable pageable);

    Page<Deck> findByIsPublicTrue(Pageable pageable);

    Page<Deck> findByCategoryRefIdAndIsPublicTrue(String categoryRefId, Pageable pageable);
}
