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
public class OllamaLlmChatService {

    private final OllamaChatModel ollamaChatModel;
    private static final String CHAT_MODEL_NAME = "llama3";

    public String getPromptResult(String prompt) {
        ChatResponse response = ollamaChatModel.call(new Prompt(prompt, OllamaOptions.create().withModel(CHAT_MODEL_NAME)));
        return response.getResult().getOutput().getContent();
    }

    public MeaningEvaluationDTO evaluate(String prompt) {
        String ollamaResponse = getPromptResult(prompt);
        log.info(ollamaResponse);
        return parseJsonString(extractJsonString(ollamaResponse));
    }

    private String extractJsonString(String input) {
        Pattern pattern = Pattern.compile("\\{.*?\\}");
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
            if (rootNode.has("confidence") && rootNode.has("explanation") && rootNode.has("isCorrect")) {
                meaningEvaluationDTO = objectMapper.readValue(jsonString, MeaningEvaluationDTO.class);
            } else {
                meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            }
        } catch (Exception e) {
            meaningEvaluationDTO = MeaningEvaluationDTO.getDefaultInstance();
            e.printStackTrace();
            log.error("Failed to evaluate the response of the user {}", e.getMessage());
        }
        return meaningEvaluationDTO;
    }

}
