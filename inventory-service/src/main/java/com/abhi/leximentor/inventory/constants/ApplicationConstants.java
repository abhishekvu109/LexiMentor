package com.abhi.leximentor.inventory.constants;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

public class ApplicationConstants {


    public static final String KAFKA_TOPIC = "word_topic";
    public static final String KAFKA_GROUP = "word-group-1";
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    public static final String REQUEST_SUCCESS_CODE = "000";
    public static final String REQUEST_SUCCESS_DESCRIPTION = "SUCCESS";
    public static final String REQUEST_FAILURE_DESCRIPTION = "FAILED";


    public static class Prompt {
        public static final String LLAMA_PROMPT = "Consider you are an evaluator of an exam. Your task is to judge if the student has answered correctly for the meaning of a word. As an input you will get the word itself, the parts of speech, the original meaning, and the meaning given my the student. Generate only a JSON like the  below {\"isCorrect\": true/false, \"confidence\": 0-100, \"explanation\": \"Something\"} isCorrect means your opinion if the answer is correct, confidence is your confidence in judging the answer, and explanation is why you feel the answer is correct or incorrect. Below is the drill- word: {word}. original meaning: {originalMeaning}. Student answer: {response}.";
        public static final String WORD = "{word}";
        public static final String ORIGINAL_MEANING = "{originalMeaning}";
        public static final String RESPONSE = "{response}";
    }

    public static class MediaType {
        public static final String APPLICATION_JSON = APPLICATION_JSON_VALUE;
        public static final String APPLICATION_XML = APPLICATION_XML_VALUE;
    }

    public static class Status {
        public static final int ACTIVE = 1;
        public static final String ACTIVE_STRING = "Active";
        public static final int INACTIVE = 0;
        public static final String INACTIVE_STRING = "Inactive";
    }
}
