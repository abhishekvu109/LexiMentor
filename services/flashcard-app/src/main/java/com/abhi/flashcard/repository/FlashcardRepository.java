package com.abhi.flashcard.repository;

import com.abhi.flashcard.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    Optional<Flashcard> findByRefId(String refId);

    List<Flashcard> findByDeckRefIdOrderByOrderIndexAsc(String deckRefId);

    long countByDeckRefId(String deckRefId);
}
