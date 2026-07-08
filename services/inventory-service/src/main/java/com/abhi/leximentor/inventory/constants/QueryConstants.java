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
            public static final String GET_SOURCE_DISTRIBUTION = "SELECT source, COUNT(*) FROM inv_word_metadata GROUP BY source";
            public static final String GET_CATEGORY_DISTRIBUTION = "SELECT category, COUNT(*) FROM inv_word_metadata GROUP BY category";
            public static final String GET_UNUSED_WORD_COUNT = "SELECT COUNT(*) FROM inv_word_metadata a WHERE a.word_id NOT IN (SELECT DISTINCT b.word_id FROM drill_set b)";

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

            public static final String GET_DRILL_TYPE_PERFORMANCE = """
                    SELECT dc.drillType AS drillType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.drillScore) AS avgScore,
                           SUM(CASE WHEN dc.isPass = true THEN 1 ELSE 0 END) AS passCount
                    FROM DrillChallenge dc
                    GROUP BY dc.drillType
                    """;

            public static final String GET_DRILL_TRENDS = """
                    SELECT DATE(crtn_date) AS day,
                           AVG(drill_score) AS avgScore,
                           COUNT(*) AS drillCount
                    FROM drill_challenge
                    WHERE (:username IS NULL OR username = :username)
                      AND crtn_date >= :fromDate
                    GROUP BY DATE(crtn_date)
                    ORDER BY day
                    """;

            public static final String GET_USER_DRILL_TYPE_PERFORMANCE = """
                    SELECT dc.drillType AS drillType,
                           COUNT(dc) AS drillCount,
                           AVG(dc.drillScore) AS avgScore,
                           SUM(CASE WHEN dc.isPass = true THEN 1 ELSE 0 END) AS passCount
                    FROM DrillChallenge dc
                    WHERE dc.username = :username
                    GROUP BY dc.drillType
                    """;

        }

        public static class WordDifficulty {
            public static final String GET_WORD_DIFFICULTY_HEATMAP = """
                    SELECT wm.ref_id AS wordRefId,
                           wm.word AS word,
                           COUNT(*) AS wrongCount
                    FROM drill_challenge_score dcs
                             INNER JOIN drill_set ds ON dcs.drill_set_id = ds.drill_set_id
                             INNER JOIN inv_word_metadata wm ON ds.word_id = wm.word_id
                    WHERE dcs.is_correct = false
                    GROUP BY wm.ref_id, wm.word
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
