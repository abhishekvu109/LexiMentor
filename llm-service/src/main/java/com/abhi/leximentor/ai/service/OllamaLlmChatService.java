package com.abhi.leximentor.ai.service;

import com.abhi.leximentor.ai.dto.MeaningEvaluationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OllamaLlmChatService implements LlmService {

    private static final String CHAT_MODEL_NAME = "llama3.2";
    private final OllamaChatModel ollamaChatModel;

    private String getPromptResult(String prompt) {
        ChatResponse response = ollamaChatModel.call(new Prompt(prompt, OllamaOptions.create().withModel(CHAT_MODEL_NAME)));
        return response.getResult().getOutput().getContent();
    }

    private MeaningEvaluationDTO evaluate(String prompt) {
        String ollamaResponse = getPromptResult(prompt);
        log.info(ollamaResponse);
        String findJsonFromResponse = extractJsonString(ollamaResponse);
        log.info("Found the JSON String from the response: {} ", findJsonFromResponse);
        return parseJsonString(findJsonFromResponse);
    }

    private String extractJsonString(String input) {
        // Pattern to match the JSON object including nested braces and newlines
        Pattern pattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "{}";
    }

    private MeaningEvaluationDTO parseJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        MeaningEvaluationDTO meaningEvaluationDTO;
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            log.info("The root node of JSON: {}", rootNode);
            if (rootNode.has("confidence") && rootNode.has("explanation") && rootNode.has("correct")) {
                log.info("Found the confidence,explanation and isCorrect Tag: {}", rootNode);
                meaningEvaluationDTO = objectMapper.readValue(jsonString, MeaningEvaluationDTO.class);
            } else {
                meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            }
        } catch (Exception e) {
            meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            e.printStackTrace();
            log.error("Failed to evaluate the response of the user {}", e.getMessage());
        }
        log.info("After the object mapper: {}", meaningEvaluationDTO);
        return meaningEvaluationDTO;
    }

    @Override
    public MeaningEvaluationDTO evaluateWordMeaning(String prompt) {
        return this.evaluate(prompt);
    }

    @Override
    public String prompt(String text) {
        ChatResponse response = ollamaChatModel.call(new Prompt(text, OllamaOptions.create().withModel(CHAT_MODEL_NAME)));
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String prompt(String text, String modelName) {
        ChatResponse response = ollamaChatModel.call(new Prompt(text, OllamaOptions.create().withModel(modelName)));
        return response.getResult().getOutput().getContent();
    }
}
