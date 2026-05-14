package com.abhi.flashcard.repository;

import com.abhi.flashcard.entity.ContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {
}
