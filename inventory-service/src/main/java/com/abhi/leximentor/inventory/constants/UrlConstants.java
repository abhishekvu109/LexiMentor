package com.abhi.leximentor.inventory.constants;

public class UrlConstants {
    public static class Drill {
        public static final String DRILL_CREATE_RANDOMLY = "/api/drill/";
        public static final String DRILL_CREATE_BY_SOURCE_RANDOMLY = "/api/drill/source/{sourceName}";
    }

    public static class Inventory {
        public static class WordMetaData {
            public static final String WORD_CREATE = "/api/inventory/words";
            public static final String WORD_GET = "/api/inventory/words/{word}";
            public static final String WORD_GET_BY_WORD_ID = "/api/inventory/words/{wordId}";
        }

        public static class Language {
            public static final String LANG_CREATE = "/api/inventory/language";
            public static final String LANG_GET = "/api/inventory/languages/{language}";
        }

        public static class Job {
            public static final String JOB_CREATE = "/api/jobs";
            public static final String JOB_EXECUTE = "/api/jobs/{jobId}";
            public static final String JOB_GET = "/api/jobs/{jobId}";
        }
    }
}
