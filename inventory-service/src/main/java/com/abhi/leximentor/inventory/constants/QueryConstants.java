package com.abhi.leximentor.inventory.constants;

public class QueryConstants {
    public static class Inventory {
        public static final String WORD_METADATA = "inv_word_metadata";

        public static class WordMetadata {
            public static final String FIND_BY_WORD = "SELECT * FROM inv_word_metadata WHERE upper(trim(word)) = :word";
            public static final String GET_WORD_RANDOMLY_IN_LIMIT = "SELECT * FROM inv_word_metadata ORDER BY RAND() LIMIT :limit";
            public static final String GET_NEW_WORD_IN_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.word_id not in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_EXISTING_WORD_IN_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.word_id in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_EXISTING_WORD_BY_SOURCE_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.source=:source and a.word_id in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_NEW_WORD_BY_SOURCE_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.source=:source and a.word_id not in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_COUNT_OF_WORDS_BY_POS = "SELECT count(*) FROM inv_parts_of_speech a WHERE a.pos=:pos";

        }

    }

    public static class Analytics {
        public static class DrillChallenge {
            public static final String GET_DRILL_CHALLENGE_METADATA = """
                    SELECT dc.drillType AS drillType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.drillScore) AS avgScore,
                           MIN(dc.drillScore) AS lowestScore,
                           MAX(dc.drillScore) AS highestScore
                    FROM DrillChallenge dc
                    GROUP BY dc.drillType
                    """;

        }
    }

    public static class NamedObject {
        public static final String GET_ACTIVE_NAMED_OBJECT = """
                SELECT * FROM named_object where status=1 LIMIT 1
                """;
    }
}
