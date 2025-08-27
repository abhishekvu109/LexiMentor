package com.abhi.leximentor.ai.constants;

public class UrlConstants {
    public static class EvaluateMeaningPrompts {
        public static final String GENERATE_PROMPT_URL = "/api/v1/llm/evaluation/meaning/{modelName}";
        public static final String GENERATE_STANDARD_PROMPT = "/api/v1/llm/text/{modelName}";

        public static class V2{
            public static final String GENERATE_PROMPT_URL = "/api/v2/llm/evaluation/meaning/{modelName}";
            public static final String GENERATE_STANDARD_PROMPT = "/api/v2/llm/text";
        }
    }
}
