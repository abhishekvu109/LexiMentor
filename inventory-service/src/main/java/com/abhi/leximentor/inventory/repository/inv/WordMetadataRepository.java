package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.constants.QueryConstants;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordMetadataRepository extends JpaRepository<WordMetadata, Long> {
    public WordMetadata findByRefId(long refId);

    @Query(value = QueryConstants.Inventory.WordMetadata.FIND_BY_WORD, nativeQuery = true)
    public WordMetadata findByWord(String word);

    public List<WordMetadata> findByRefIdIn(List<Long> refIds);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_WORD_RANDOMLY_IN_LIMIT, nativeQuery = true)
    public List<WordMetadata> findAllRandomlyInLimit(int limit);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_NEW_WORD_IN_LIMIT, nativeQuery = true)
    public List<WordMetadata> findAllRandomlyNewWordsLimit(int limit);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_EXISTING_WORD_IN_LIMIT, nativeQuery = true)
    public List<WordMetadata> findAllRandomlyExistingWordsLimit(int limit);


    @Query(value = QueryConstants.Inventory.WordMetadata.GET_EXISTING_WORD_BY_SOURCE_LIMIT, nativeQuery = true)
    public List<WordMetadata> findAllRandomlyExistingWordsFromSourceInLimit(int limit, String source);

    @Query(value = QueryConstants.Inventory.WordMetadata.GET_NEW_WORD_BY_SOURCE_LIMIT, nativeQuery = true)
    public List<WordMetadata> findAllRandomlyNewWordsFromSourceInLimit(int limit, String source);


}
