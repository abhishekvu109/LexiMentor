package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.WordMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WordRepository extends JpaRepository<WordMetadata, Long> {
    public WordMetadata findByKey(String bKey);

    @Query(value = "SELECT * FROM word_metadata  WHERE upper(trim(word)) = :word", nativeQuery = true)
    public WordMetadata findByWord(String word);
}
