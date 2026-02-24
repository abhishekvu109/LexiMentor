package com.abhi.leximentor.leximentor.repository.inv;

import com.abhi.leximentor.leximentor.constants.QueryConstants;
import com.abhi.leximentor.leximentor.entities.inv.WordMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordMetadataRepository extends JpaRepository<WordMetadata, Long> {
    Optional<WordMetadata> findByKey(String key);

    @Query(value = QueryConstants.Inventory.WordMetadata.FIND_BY_WORD, nativeQuery = true)
    Optional<WordMetadata> findByWord(String word);

    List<WordMetadata> findByKeyIn(List<String> keys);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_WORD_RANDOMLY_IN_LIMIT, nativeQuery = true)
    List<WordMetadata> findAllRandomlyInLimit(int limit);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_NEW_WORD_IN_LIMIT, nativeQuery = true)
    List<WordMetadata> findAllRandomlyNewWordsLimit(int limit);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_EXISTING_WORD_IN_LIMIT, nativeQuery = true)
    List<WordMetadata> findAllRandomlyExistingWordsLimit(int limit);


    @Query(value = QueryConstants.Inventory.WordMetadata.GET_EXISTING_WORD_BY_SOURCE_LIMIT, nativeQuery = true)
    List<WordMetadata> findAllRandomlyExistingWordsFromSourceInLimit(int limit, String source);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_NEW_WORD_BY_SOURCE_LIMIT, nativeQuery = true)
    List<WordMetadata> findAllRandomlyNewWordsFromSourceInLimit(int limit, String source);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_COUNT_OF_WORDS_BY_POS, nativeQuery = true)
    int findCountOfWordsByPos(@Param("pos") String pos);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_SOURCE_DISTRIBUTION, nativeQuery = true)
    List<Object[]> findSourceDistribution();

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_CATEGORY_DISTRIBUTION, nativeQuery = true)
    List<Object[]> findCategoryDistribution();

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_UNUSED_WORD_COUNT, nativeQuery = true)
    long countUnusedWords();
}
