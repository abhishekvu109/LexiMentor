package com.abhi.leximentor.inventory.constants;

import com.abhi.leximentor.inventory.entities.inv.WordMetadata;

public class QueryConstants {
    public static class Inventory {
        public static final String WORD_METADATA = "inv_word_metadata";

        public static class WordMetadata {
            public static final String FIND_BY_WORD = "SELECT * FROM inv_word_metadata WHERE upper(trim(word)) = :word";
            public static final String GET_WORD_RANDOMLY_IN_LIMIT = "SELECT * FROM inv_word_metadata ORDER BY RAND() LIMIT :limit";
            public static final String GET_NEW_WORD_IN_LIMIT = "SELECT * FROM inv_word_metadata a, drill_set b where a.word_id!=b.word_id LIMIT :limit";
            public static final String GET_EXISTING_WORD_IN_LIMIT = "SELECT * FROM inv_word_metadata a, drill_set b where a.word_id=b.word_id LIMIT :limit";
            public static final String GET_EXISTING_WORD_BY_SOURCE_LIMIT = "SELECT * FROM inv_word_metadata a, drill_set b where a.word_id=b.word_id and a.source=:source LIMIT :limit";
            public static final String GET_NEW_WORD_BY_SOURCE_LIMIT = "SELECT * FROM inv_word_metadata a, drill_set b where a.word_id!=b.word_id and a.source=:source LIMIT :limit";
        }
    }
}
