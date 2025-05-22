package com.abhi.leximentor.ai.constants;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

public class ApplicationConstants {


    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    public static final String REQUEST_SUCCESS_CODE = "000";
    public static final String REQUEST_SUCCESS_DESCRIPTION = "SUCCESS";
    public static final String REQUEST_FAILURE_DESCRIPTION = "FAILED";


    public static class MediaType {
        public static final String APPLICATION_JSON = APPLICATION_JSON_VALUE;
        public static final String APPLICATION_XML = APPLICATION_XML_VALUE;
    }

    public static class AiServices {
        public static final String LLAMA = "llama-llm-based-evaluator";
        public static final String OLLAMA = "ollama-llm-based-evaluator";
    }

    public static final String LLM_EVALUATION_RESPONSE_FORMAT = """
            {
              "type": "object",
              "properties": {
                "spelling": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "grammar": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "punctuation": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "vocabulary": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "styleAndTone": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "creativityAndThinking": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                }
              },
              "required": [
                "spelling",
                "grammar",
                "punctuation",
                "vocabulary",
                "styleAndTone",
                "creativityAndThinking"
              ]
            }
                        
            """;
}
