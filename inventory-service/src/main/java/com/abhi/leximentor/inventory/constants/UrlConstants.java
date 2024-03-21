package com.abhi.leximentor.inventory.constants;

public class UrlConstants {
    public static class Drill {
        public static class DrillMetadata {
        }

        public static class DrillChallenge {
        }

        public static class DrillChallengeScores {
        }

        public static class DrillSet {
        }

        public static final String DRILL_ADD = "/api/drill";
        public static final String DRILL_ADD_BY_SOURCE = "/api/drill/source/{sourceName}";
        public static final String DRILL_ASSIGN_CHALLENGES_TO_DRILLS = "/api/drill/challenges";
        public static final String DRILL_GET_CHALLENGES_BY_DRILL_ID = "/api/drill/challenges/{drillId}";
        public static final String DRILL_GET_CHALLENGE_SCORE_BY_CHALLENGE_ID = "/api/drill/challenges/scores/{challengeId}";
        public static final String DRILL_GET_DRILL_SET_BY_SET_ID = "/api/drill/challenges/sets/set/{setId}";
        public static final String DRILL_GET_DRILL_SETS_BY_DRILL_ID = "/api/drill/challenges/sets/{drillRefId}";
        public static final String DRILL_EVALUATE_BY_DRILL_CHALLENGE_ID = "/api/drill/challenges/challenge/evaluate/{challengeId}";

        public static class DrillChallengeScore {
            public static final String SUBMIT_MEANING_RESPONSE = "/api/drill/challenges/scores";
        }
    }

    public static class Inventory {
        public static class WordMetaData {
            public static final String WORD_ADD_WORDS = "/api/inventory/words";
            public static final String WORD_GET_BY_WORD = "/api/inventory/words/{word}";
            public static final String WORD_GET_BY_WORD_REF_ID = "/api/inventory/words/{wordRefId}";
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
