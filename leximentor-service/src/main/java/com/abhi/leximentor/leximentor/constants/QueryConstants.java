package com.abhi.leximentor.leximentor.constants;

public class QueryConstants {
    public static class Inventory {
        public static final String WORD_METADATA = "inv_word_metadata";

        public static class WordMetadata {
            public static final String FIND_BY_WORD = "SELECT * FROM inv_word_metadata WHERE upper(trim(word)) = :word";
            public static final String GET_WORD_RANDOMLY_IN_LIMIT = "SELECT * FROM inv_word_metadata ORDER BY RAND() LIMIT :limit";
            public static final String GET_NEW_WORD_IN_LIMIT = """
                     SELECT a.*
                     FROM   inv_word_metadata a
                     WHERE  a.id NOT IN (SELECT DISTINCT( b.word_id )
                                              FROM   drill_set b)
                     ORDER  BY Rand()
                     LIMIT  :limit
                    """;
            public static final String GET_EXISTING_WORD_IN_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.id in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_EXISTING_WORD_BY_SOURCE_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.source=:source and a.id in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_NEW_WORD_BY_SOURCE_LIMIT = "SELECT a.* FROM inv_word_metadata a where a.source=:source and a.id not in (select distinct(b.word_id) from drill_set b) order by RAND() LIMIT :limit";
            public static final String GET_COUNT_OF_WORDS_BY_POS = "SELECT count(*) FROM parts_of_speech a WHERE a.pos=:pos";
            public static final String GET_SOURCE_DISTRIBUTION = "SELECT source, COUNT(*) FROM inv_word_metadata GROUP BY source";
            public static final String GET_CATEGORY_DISTRIBUTION = "SELECT category, COUNT(*) FROM inv_word_metadata GROUP BY category";
            public static final String GET_UNUSED_WORD_COUNT = "SELECT COUNT(*) FROM inv_word_metadata a WHERE a.id NOT IN (SELECT DISTINCT b.word_id FROM drill_set b)";

        }

    }

    public static class Analytics {
        public static class Challenge {
            public static final String GET_CHALLENGE_METADATA = """
                    SELECT dc.challengeType AS challengeType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.score) AS avgScore,
                           MIN(dc.score) AS lowestScore,
                           MAX(dc.score) AS highestScore
                    FROM Challenge dc
                    GROUP BY dc.challengeType
                    """;

            public static final String GET_DRILL_TYPE_PERFORMANCE = """
                    SELECT dc.challengeType AS challengeType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.score) AS avgScore,
                           SUM(CASE WHEN dc.isPass = true THEN 1 ELSE 0 END) AS passCount
                    FROM Challenge dc
                    GROUP BY dc.challengeType
                    """;

            public static final String GET_DRILL_TRENDS = """
                    SELECT DATE(created_at) AS day,
                           AVG(score) AS avgScore,
                           COUNT(*) AS drillCount
                    FROM challenge
                    WHERE (:username IS NULL OR username = :username)
                      AND created_at >= :fromDate
                    GROUP BY DATE(created_at)
                    ORDER BY day
                    """;

            public static final String GET_USER_DRILL_TYPE_PERFORMANCE = """
                    SELECT dc.challengeType AS drillType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.score) AS avgScore,
                           SUM(CASE WHEN dc.isPass = true THEN 1 ELSE 0 END) AS passCount
                    FROM Challenge dc
                    WHERE dc.username = :username
                    GROUP BY dc.challengeType
                    """;

        }

        public static class WordDifficulty {
            public static final String GET_WORD_DIFFICULTY_HEATMAP = """
                    SELECT wm.key AS wordKey,
                           wm.word AS word,
                           COUNT(*) AS wrongCount
                    FROM challenge_score dcs
                             INNER JOIN drill_set ds ON dcs.drill_set_id = ds.id
                             INNER JOIN inv_word_metadata wm ON ds.word_id = wm.id
                    WHERE dcs.is_correct = false
                    GROUP BY wm.key, wm.word
                    ORDER BY wrongCount DESC
                    LIMIT :limit
                    """;
        }
    }

    public static class NamedObject {
        public static final String GET_ACTIVE_NAMED_OBJECT = """
                SELECT * FROM named_object where status=1 LIMIT 1
                """;
    }
}
