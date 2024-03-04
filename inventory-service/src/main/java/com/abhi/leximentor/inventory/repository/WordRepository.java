package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.WordMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<WordMetadata, Long> {
    public WordMetadata findByKey(String bKey);

    public WordMetadata findByWord(String word);
}
