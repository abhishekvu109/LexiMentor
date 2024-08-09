package com.abhi.leximentor.ai;

import com.abhi.leximentor.ai.dto.MeaningEvaluationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {
    private static String extractJsonString(String input) {
        // Pattern to match the JSON object including nested braces and newlines
        Pattern pattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "{}";
    }
    private static MeaningEvaluationDTO parseJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        MeaningEvaluationDTO meaningEvaluationDTO;
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (rootNode.has("confidence") && rootNode.has("explanation") && rootNode.has("isCorrect")) {
                meaningEvaluationDTO = objectMapper.readValue(jsonString, MeaningEvaluationDTO.class);
            } else {
                meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            }
        } catch (Exception e) {
            meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            e.printStackTrace();
        }
        return meaningEvaluationDTO;
    }

    public static void main(String[] args) {
        String s = """
                                
                Here's my evaluation:
                                
                {
                "isCorrect": true,
                "confidence": 95,
                "explanation": "The student's definition matches the official definition quite closely. The only slight difference is the word 'mania' in the official definition, which suggests a more intense or obsessive behavior. However, this nuance is minor, and overall, the student's response accurately conveys the meaning of the word 'pyromaniac'."
                }
                                
                I'm 95% confident that the student has correctly defined the word because their definition captures the core idea of setting things on fire, which is the central aspect of being a pyromaniac. The only minor variation is in the use of 'mania' versus 'obsessed', but this doesn't significantly alter the overall meaning.
                """;
        String jsonStr=extractJsonString(s);
        MeaningEvaluationDTO dto=parseJsonString(jsonStr);
        System.out.println(dto);
    }
}
