package com.abhi.leximentor.inventory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMPromptBuilder {
    private static final String DEFAULT_PROMPT = "Consider you are evaluating a student's understanding of vocabulary. Your task is to assess if the student has correctly defined a given word. You will receive the word itself, its part of speech, the official definition, and the student's interpretation. Please provide your evaluation in a JSON format as follows:";
    private static final String NEW_LINE = "\n";
    private static final String FINAL_PROMPT = "Evaluate the student's response based on the provided information. Assign true to isCorrect if the student's definition accurately reflects the meaning of the word. Assign false otherwise. Use the confidence score to indicate how certain you are about your judgment (0 for least confident, 100 for most confident). Provide an explanation to justify your decision.";

    private static class OutputLLM {
        public boolean isCorrect = true;
        public int confidence = 0;
        public String explanation = "Explanation of your decision";
    }

    private static String json() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String output = "";
        try {
            output = objectMapper.writeValueAsString(new OutputLLM());
        } catch (Exception ex) {
            log.error("Unable to deserialize the JSON");
        }
        return output;
    }

    public static synchronized String getPrompt(String word, String officialMeaning, String candidateResponse) {
        return DEFAULT_PROMPT + json() + NEW_LINE + "Here's the scenario:" + NEW_LINE + "word: " + word + NEW_LINE + "Official Definition:" + officialMeaning + NEW_LINE + "Student's response:" + candidateResponse + NEW_LINE + NEW_LINE + FINAL_PROMPT;
    }
}
