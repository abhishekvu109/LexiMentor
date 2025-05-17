package com.abhi.writewise.inventory.constants;

public class UrlConstants {
    public static class Topic {

        public static final String BASE_URL="/api/writewise";
        public static class V1{
            public static final String DELETE_ALL_TOPICS = "/v1/topics";
            public static final String GENERATE_TOPICS = "/v1/topics";
            public static final String GET_TOPIC_BY_TOPIC_ID = "/v1/topics/topic/{topicRefId}";
            public static final String DELETE_TOPIC_BY_TOPIC_ID = "/v1/topics/topic/{topicRefId}";
            public static final String GENERATE_WORD_METADATA = "/v1/word/metadata";
        }

    }
    public static class ResponseAndEvaluate{
        public static final String CAPTURE_RESPONSE= "/api/writewise/v1/response";
        public static final String RESPONSE_EVALUATE= "/api/writewise/v1/evaluate";
    }
}
