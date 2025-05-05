package com.abhi.leximentor.inventory.constants;

public class UrlConstants {
    public static class Drill {
        public static class DrillMetadata {
            public static final String DRILL_METADATA_ADD = "/api/leximentor/drill/metadata";
            public static final String DRILL_METADATA_ADD_BY_SOURCE = "/api/leximentor/drill/metadata/source/{sourceName}";
            public static final String DRILL_METADATA_DELETE_BY_REF_ID = "/api/leximentor/drill/metadata/{drillRefId}";
            public static final String DRILL_METADATA_GET_WORDS_BY_REF_ID = "/api/leximentor/drill/metadata/words/{drillRefId}";

            public static final String DRILL_METADATA_ASSIGN_NAME = "/api/leximentor/drill/metadata/assign-name";


        }

        public static class DrillChallenge {
            //            Add challenges to the drill.
            public static final String DRILL_CHALLENGE_ADD = "/api/leximentor/drill/metadata/challenges/challenge";
            public static final String DRILL_GET_CHALLENGES_BY_DRILL_ID = "/api/leximentor/drill/metadata/challenges/{drillRefId}";
            public static final String DRILL_GET_EVALUATORS_BY_CHALLENGE_REF_ID = "/api/leximentor/drill/metadata/challenges/challenge/evaluators";
            public static final String DRILL_DELETE_CHALLENGES_BY_DRILL_ID = "/api/leximentor/drill/metadata/challenges/{drillRefId}";
        }

        public static class DrillChallengeScores {
            public static final String DRILL_GET_CHALLENGE_SCORES_BY_CHALLENGE_ID = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/scores";
            public static final String DRILL_UPDATE_CHALLENGE_SCORES_BY_CHALLENGE_ID = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/scores";
        }

        public static class DrillSet {
            public static final String DRILL_GET_DRILL_SET_BY_SET_ID = "/api/leximentor/drill/metadata/sets/set/{drillSetRefId}";
            public static final String DRILL_GET_DRILL_SETS_BY_DRILL_ID = "/api/leximentor/drill/metadata/sets/{drillRefId}";
            public static final String DRILL_GET_WORDS_BY_DRILL_ID = "/api/leximentor/drill/metadata/sets/words/{drillRefId}";
            public static final String DRILL_GET_WORDS_DATA_BY_DRILL_ID = "/api/leximentor/drill/metadata/sets/words/data/{drillRefId}";
        }

        public static class DrillEvaluation {
            public static final String DRILL_EVALUATE_BY_CHALLENGE_ID = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/evaluate";
            public static final String DRILL_GET_EVALUATION_RESULT_BY_CHALLENGE_ID = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/report";
        }

        public static class DrillAnalytics {
            public static final String DRILL_GET_ANALYTICS_DRILL_REF_ID = "/api/leximentor/analytics/drill/{drillRefId}";
            public static final String DRILL_GET_DRILL_CHALLENGE_METADATA_ANALYTICS = "/api/leximentor/analytics/drill/challenge/metadata";
        }

    }

    public static class Inventory {
        public static class WordMetaData {
            public static final String WORD_ADD_WORDS = "/api/leximentor/inventory/words";
            public static final String WORD_GENERATE_METADATA_FROM_LLM = "/api/leximentor/inventory/words/generate";
            public static final String WORD_GET_BY_WORD = "/api/leximentor/inventory/words/{word}";
            public static final String WORD_GET_BY_WORD_REF_ID = "/api/leximentor/inventory/words/{wordRefId}";
            public static final String WORD_GET_SOURCES_BY_WORD_REF_ID = "/api/leximentor/inventory/words/{wordRefId}/sources";
            public static final String WORD_GET_BY_WORD_REF_ID_AND_SOURCES = "/api/leximentor/inventory/words/{wordRefId}/sources/{source}";
        }

        public static class Language {
            public static final String LANG_CREATE = "/api/leximentor/inventory/language";
            public static final String LANG_GET = "/api/leximentor/inventory/languages/{language}";
        }

        public static class Evaluator {
            public static final String EVALUATOR_CREATE = "/api/leximentor/evaluators/evaluator";
            public static final String EVALUATOR_GET_BY_REF = "/api/leximentor/evaluators/evaluator/id/{refId}";
            public static final String EVALUATOR_GET_BY_NAME = "/api/leximentor/evaluators/evaluator/name/{name}";
            public static final String EVALUATOR_GET_BY_DRILL_TYPE = "/api/leximentor/evaluators/evaluator/type/drill/{drillType}";
        }

        public static class Job {
            public static final String JOB_CREATE = "/api/leximentor/jobs";
            public static final String JOB_EXECUTE = "/api/leximentor/jobs/{jobId}";
            public static final String JOB_GET = "/api/leximentor/jobs/{jobId}";
        }

        public static class WritingModule {
            public static final String GENERATE_TOPICS = "/api/leximentor/v1/module/writing/topics";
        }
    }

    public static class NamedObject{
        public static final  String ADD_NAMED_OBJECT="/api/leximentor/v1/named/object";
    }
}
