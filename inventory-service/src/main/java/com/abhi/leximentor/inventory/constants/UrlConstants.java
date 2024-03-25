package com.abhi.leximentor.inventory.constants;

public class UrlConstants {
    public static class Drill {
        public static class DrillMetadata {
            public static final String DRILL_METADATA_ADD = "/api/drill/metadata";
            public static final String DRILL_METADATA_ADD_BY_SOURCE = "/api/drill/metadata/source/{sourceName}";
            public static final String DRILL_METADATA_DELETE_BY_REF_ID = "/api/drill/metadata/{drillRefId}";

        }

        public static class DrillChallenge {
            //            Add challenges to the drill.
            public static final String DRILL_CHALLENGE_ADD = "/api/drill/metadata/challenges/challenge";
            public static final String DRILL_GET_CHALLENGES_BY_DRILL_ID = "/api/drill/metadata/challenges/{drillRefId}";
        }

        public static class DrillChallengeScores {
            public static final String DRILL_GET_CHALLENGE_SCORES_BY_CHALLENGE_ID = "/api/drill/metadata/challenges/challenge/{challengeId}/scores";
            public static final String DRILL_UPDATE_CHALLENGE_SCORES_BY_CHALLENGE_ID = "/api/drill/metadata/challenges/challenge/{challengeId}/scores";
        }

        public static class DrillSet {
            public static final String DRILL_GET_DRILL_SET_BY_SET_ID = "/api/drill/metadata/sets/set/{drillSetRefId}";
            public static final String DRILL_GET_DRILL_SETS_BY_DRILL_ID = "/api/drill/metadata/sets/{drillRefId}";
        }

        public static class DrillEvaluation {
            public static final String DRILL_EVALUATE_BY_CHALLENGE_ID = "/api/drill/metadata/challenges/challenge/{challengeId}/evaluate";
            public static final String DRILL_GET_EVALUATION_RESULT_BY_CHALLENGE_ID = "/api/drill/metadata/challenges/challenge/{challengeId}/report";
        }

    }

    public static class Inventory {
        public static class WordMetaData {
            public static final String WORD_ADD_WORDS = "/api/inventory/words";
            public static final String WORD_GET_BY_WORD = "/api/inventory/words/{word}";
            public static final String WORD_GET_BY_WORD_REF_ID = "/api/inventory/words/{wordRefId}";
            public static final String WORD_GET_SOURCES_BY_WORD_REF_ID = "/api/inventory/words/{wordRefId}/sources";
            public static final String WORD_GET_BY_WORD_REF_ID_AND_SOURCES = "/api/inventory/words/{wordRefId}/sources/{source}";
        }

        public static class Language {
            public static final String LANG_CREATE = "/api/inventory/language";
            public static final String LANG_GET = "/api/inventory/languages/{language}";
        }

        public static class Evaluator {
            public static final String EVALUATOR_CREATE = "/api/evaluators/evaluator";
            public static final String EVALUATOR_GET_BY_REF = "/api/evaluators/evaluator/id/{refId}";
            public static final String EVALUATOR_GET_BY_NAME = "/api/evaluators/evaluator/name/{name}";
            public static final String EVALUATOR_GET_BY_DRILL_TYPE = "/api/evaluators/evaluator/type/drill/{drillType}";
        }

        public static class Job {
            public static final String JOB_CREATE = "/api/jobs";
            public static final String JOB_EXECUTE = "/api/jobs/{jobId}";
            public static final String JOB_GET = "/api/jobs/{jobId}";
        }
    }
}
