package com.abhi.writewise.inventory.constants;

import java.util.HashMap;
import java.util.Map;

public class Status {
    public static class Topic {
        public static final Integer TOPIC_REQUEST = 1;
        public static final Integer TOPIC_RESPONSE = 2;
        public static final Integer USER_RESPONDED = 3;
        public static final Integer LLM_EVALUATED = 4;
        private static final Map<Integer, String> statusMap;
        private static final Map<Integer, String> statusShortMap;

        static {
            statusMap = new HashMap<>();
            statusMap.put(TOPIC_REQUEST, "The user has requested through a prompt to generate the topics.");
            statusMap.put(TOPIC_RESPONSE, "The LLM has responded in JSON format.");
            statusMap.put(USER_RESPONDED, "The user has chosen the topic and submitted the response for evaluation.");
            statusMap.put(LLM_EVALUATED, "The LLM has evaluated and given the feedback.");

            statusShortMap = new HashMap<>();
            statusShortMap.put(TOPIC_REQUEST, "Generating topics");
            statusShortMap.put(TOPIC_RESPONSE, "Topics generated");
            statusShortMap.put(USER_RESPONDED, "Topic selected");
            statusShortMap.put(LLM_EVALUATED, "Evaluation completed");
        }

        public static String getMessage(Integer status) {
            return statusMap.get(status);
        }

        public static String getStatusStr(Integer status) {
            return statusShortMap.get(status);
        }

        public static class DeleteStatus {
            public static final int ACTIVE = 1;
            public static final int INACTIVE = 0;

            public static String getStatus(int status) {
                return (status == ACTIVE) ? "Active" : "Inactive";
            }
        }
    }

    public static class TopicResponse {
        public static final Integer NOT_STARTED = 1;
        public static final Integer IN_PROGRESS = 2;
        public static final Integer SUBMITTED = 3;

        public static final Map<Integer, String> statusIntToStr;

        static {
            statusIntToStr = new HashMap<>();
            statusIntToStr.put(1, "Not Started");
            statusIntToStr.put(2, "In-progress");
            statusIntToStr.put(3, "Submitted");
        }

        public static String getString(int status) {
            return statusIntToStr.get(status);
        }
    }

    public static class EvaluationStatus {
        public static final Integer NOT_STARTED = 1;
        public static final Integer IN_PROGRESS = 2;
        public static final Integer COMPLETED = 3;

        public static final Map<Integer, String> statusIntToStr;

        static {
            statusIntToStr = new HashMap<>();
            statusIntToStr.put(1, "Not Started");
            statusIntToStr.put(2, "In-progress");
            statusIntToStr.put(3, "Completed");
        }

        public static String getString(int status) {
            return statusIntToStr.get(status);
        }

    }
}
