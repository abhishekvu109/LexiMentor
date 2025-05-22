package com.abhi.leximentor.ai.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMPromptServiceImpl implements LLMPromptService {
    @Value("${llm-base-url}")
    private String BASE_URL;

    @Value("${llm-model-name}")
    private String MODEL_NAME;

    @Override
    public String execute(String prompt) {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder().baseUrl(BASE_URL).modelName(MODEL_NAME).timeout(Duration.ofHours(1)).maxRetries(2).build();
        return chatLanguageModel.generate(prompt);
    }

    @Override
    public String execute(String prompt, String format) {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder().baseUrl(BASE_URL).modelName(MODEL_NAME).format(StringUtils.isNotEmpty(format) ? format : "").timeout(Duration.ofHours(1)).maxRetries(2).build();
        return chatLanguageModel.generate(prompt);
    }

    @Override
    public String execute(String prompt, String format, String model) {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder().baseUrl(BASE_URL).modelName(StringUtils.isNotEmpty(model) ? model : MODEL_NAME).format(StringUtils.isNotEmpty(format) ? format : "").timeout(Duration.ofHours(1)).maxRetries(2).build();
        return chatLanguageModel.generate(prompt);
    }
}
