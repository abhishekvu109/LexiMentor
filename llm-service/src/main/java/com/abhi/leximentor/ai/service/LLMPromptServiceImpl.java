package com.abhi.leximentor.ai.service;

import com.abhi.leximentor.ai.dto.OllamaDTO;
import com.abhi.leximentor.ai.dto.OllamaResponseDTO;
import com.abhi.leximentor.ai.util.RestClient;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMPromptServiceImpl implements LLMPromptService {
    @Value("${llm-base-url}")
    private String BASE_URL;
    @Value("${ollama-llm-base-url}")
    private String OLLAMA_BASE_URL;

    @Value("${llm-model-name}")
    private String MODEL_NAME;

    private final RestClient restClient;

    @Override
    public String execute(String prompt) {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder().baseUrl(BASE_URL).modelName(MODEL_NAME).timeout(Duration.ofHours(1)).maxRetries(2).build();
        return chatLanguageModel.generate(prompt);
    }

    @Override
    public OllamaResponseDTO execute(OllamaDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<OllamaResponseDTO> responseEntity;
        OllamaResponseDTO responseOutput;
        try {
            responseEntity = restClient.post(OLLAMA_BASE_URL, headers, request, OllamaResponseDTO.class);
            responseOutput = responseEntity.getBody();
            log.info("The llm service service has returned a response : {}", responseEntity);
        } catch (Exception ex) {
            log.error("Unable to get response from the llm service {} for {}", OLLAMA_BASE_URL, request);
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        return responseOutput;
    }

    @Override
    public String execute(String prompt, String format, String model) {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder().baseUrl(BASE_URL).modelName(StringUtils.isNotEmpty(model) ? model : MODEL_NAME).format(StringUtils.isNotEmpty(format) ? format : "").timeout(Duration.ofHours(1)).maxRetries(2).build();
        return chatLanguageModel.generate(prompt);
    }
}
