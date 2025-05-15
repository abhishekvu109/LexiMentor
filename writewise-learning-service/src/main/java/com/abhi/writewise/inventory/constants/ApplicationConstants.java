package com.abhi.writewise.inventory.constants;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

public class ApplicationConstants {


    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;
    public static final int MIN_DRILL_SIZE = 5;

    public static final String REQUEST_SUCCESS_CODE = "000";
    public static final String REQUEST_SUCCESS_DESCRIPTION = "SUCCESS";
    public static final String REQUEST_FAILURE_DESCRIPTION = "FAILED";


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

    public static class EvaluationCategory {
        public static final int SPELLING = 1;
        public static final String SPELLING_STR = "spelling";
        public static final int GRAMMAR = 2;
        public static final String GRAMMAR_STR = "grammar";

        public static final int PUNCTUATION = 3;
        public static final String PUNCTUATION_STR = "punctuation";

        public static final int VOCABULARY = 4;
        public static final String VOCABULARY_STR = "vocabulary";

        public static final int STYLE_AND_TONE = 5;
        public static final String STYLE_AND_TONE_STR = "styleAndTone";

        public static final int CREATIVITY_AND_THINKING = 6;
        public static final String CREATIVITY_AND_THINKING_STR = "creativityandthing";


        private static final Map<Integer, String> intToStrMap;
        private static final Map<Integer, Double> weights;
        private static final Map<String, Integer> strToInt;

        static {
            intToStrMap = new HashMap<>();
            strToInt = new HashMap<>();
            weights = new HashMap<>();
            intToStrMap.put(SPELLING, SPELLING_STR);
            intToStrMap.put(GRAMMAR, GRAMMAR_STR);
            intToStrMap.put(PUNCTUATION, PUNCTUATION_STR);
            intToStrMap.put(VOCABULARY, VOCABULARY_STR);
            intToStrMap.put(STYLE_AND_TONE, STYLE_AND_TONE_STR);
            intToStrMap.put(CREATIVITY_AND_THINKING, CREATIVITY_AND_THINKING_STR);

            strToInt.put(SPELLING_STR, SPELLING);
            strToInt.put(GRAMMAR_STR, GRAMMAR);
            strToInt.put(PUNCTUATION_STR, PUNCTUATION);
            strToInt.put(VOCABULARY_STR, VOCABULARY);
            strToInt.put(STYLE_AND_TONE_STR, STYLE_AND_TONE);
            strToInt.put(CREATIVITY_AND_THINKING_STR, CREATIVITY_AND_THINKING);

            weights.put(SPELLING, 0.15);
            weights.put(GRAMMAR, 0.30);
            weights.put(PUNCTUATION, 0.30);
            weights.put(VOCABULARY, 0.05);
            weights.put(STYLE_AND_TONE, 0.05);
            weights.put(CREATIVITY_AND_THINKING, 0.15);
        }

        public static String getString(int status) {
            return intToStrMap.get(status);
        }

        public static int getInt(String status) {
            return strToInt.get(status.trim().toLowerCase());
        }

        public static double getWeight(int category) {
            return weights.get(category);
        }

    }
}
