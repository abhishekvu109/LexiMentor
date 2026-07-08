package com.abhi.writewise.inventory.constants;

public class UrlConstants {
    public static class Topic {

        public static final String BASE_URL = "/api/writewise";

        public static class V1 {
            public static final String DELETE_ALL_TOPIC_GENERATIONS = "/v1/topic-generations";
            public static final String GENERATE_TOPIC_GENERATIONS = "/v1/topic-generations";
            public static final String FIND_TOPIC_GENERATION_TOPIC_GENERATION_REF_ID = "/v1/topic-generations/topic-generation/{topicGenerationRefId}";
            public static final String GENERATE_ALL_TOPICS = "/v1/topics";
            public static final String GET_TOPIC_BY_TOPIC_ID = "/v1/topics/topic/{topicRefId}";
            public static final String DELETE_TOPIC_GENERATION_BY_TOPIC_GENERATION_REF_ID = "/v1/topic-generations/topic-generation/{topicGenerationRefId}";
            public static final String GENERATE_WORD_METADATA = "/v1/word/metadata";
            public static final String CREATE_TOPIC_MANUALLY = "/v1/topics/create-manually";
        }

    }

    public static class ResponseAndEvaluate {
        public static final String CAPTURE_RESPONSE = "/api/writewise/v1/response";
        public static final String GET_RESPONSES_TOPIC_REF_ID = "/api/writewise/v1/response/response-version/{topicRefId}";
        public static final String GET_RESPONSES_SUBMITTED = "/api/writewise/v1/response/submitted-responses";
        public static final String GET_RESPONSES_EVALUATED = "/api/writewise/v1/response/evaluated-responses";
    }

    public static class ResponseVersion {
        public static final String BASE_URL = "/api/writewise";

        public static class V1 {
            public static final String GENERATE_PROMPT = "/v1/topics/topic/{topicRefId}/versions/version/{versionRefId}/generate-prompt";
            public static final String EVALUATE_RESPONSE = "/v1/topics/topic/versions/version/evaluate-response";
            public static final String VALIDATE_EVALUATION_RESPONSE = "/v1/topics/topic/versions/version/evaluate-response/validate";
            public static final String SUBMIT_EVALUATION_RESULTS = "/v1/topics/topic/versions/version/submit-results";
            public static final String GET_RESPONSE_VERSION_BY_TOPIC_REF_ID = "/v1/topics/topic/{topicRefId}/versions/version/{versionRefId}";
            public static final String DELETE_ALL_EVALUATIONS_FOR_TOPIC = "/v1/topics/topic/{topicRefId}/versions/version/delete-evaluations";
            public static final String DELETE_SELECTED_EVALUATIONS_FOR_TOPIC = "/v1/topics/topic/{topicRefId}/versions/version/{versionRefId}/delete-evaluations";
            public static final String DELETE_RESPONSE_VERSION = "/v1/topics/topic/{topicRefId}/versions/version/{versionRefId}";
        }
    }

    public static class Response {
        public static final String BASE_URL = "/api/writewise";

        public static class V1 {
            public static final String GET_RESPONSE_BY_TOPIC_AND_VERSION = "/api/writewise/v1/responses/response/{responseRefId}/versions/version/{versionRefId}";
        }
    }
}
