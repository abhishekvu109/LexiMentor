package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.dto.WordDefinitionDTO;
import com.abhi.writewise.inventory.service.WordDefinitionService;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.abhi.writewise.inventory.util.RestClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordDefinitionServiceImpl implements WordDefinitionService {
    private final Integer RETRY_COUNT = 3;
    private final static String LLM_TOPIC = "ollama-llm-writing-module-topics";
    private String url;
    private final RestClient restClient;
    private static final String MODEL_NAME = "deepseek-r1:7b";

    @Override
    public WordDefinitionDTO generateWordDefinitionFromLlm(WordDefinitionDTO request) {
        log.info("LLM word definition service is called.");
        loadModelServiceName();
        String prompt = "";
        if (CollectionUtils.isNotEmpty(request.getWords())) {
            StringBuilder words = new StringBuilder();
            for (String word : request.getWords()) {
                words.append("\n\t").append("<word>").append(word).append("</word>");
            }
            prompt = LLMPromptBuilder.WordDefinitionPrompt.prompt(words.toString());
        } else {
            throw new RuntimeException("No words words");
        }
        request.setPrompt(prompt);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<String> responseEntity;
        String responseOutput = null;
        int retry = RETRY_COUNT;
        while (retry > 0) {
            try {
                responseEntity = restClient.post(url, headers, request, String.class);
                responseOutput = responseEntity.getBody();
                log.info("The llm service has returned a response : {}", responseEntity);
                break;
            } catch (Exception ex) {
                log.error("Unable to get response from the llm service {} for {}", LLM_TOPIC, request);
                log.error(ex.getMessage());
                log.info("Attempting retry : {}", (RETRY_COUNT - retry));
                retry--;
            }
        }
        String llmResponse = extractJsonFromResponse(responseOutput);
//        log.info("LLM has generated the response. {}", llmResponse);
        log.info("LLM has generated the response. {}", responseOutput);
        if (StringUtils.isNotEmpty(llmResponse)) {
            request.setPrompt(prompt);
            request.setResponse(llmResponse);
        }
        return request;
    }

    private void loadModelServiceName() {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
            log.info("Successfully found the llm topic address: {}", properties.getProperty(LLM_TOPIC));
            setUrl(properties.getProperty(LLM_TOPIC) + MODEL_NAME);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }


    private String extractJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("<response>(.*?)</response>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        throw new IllegalArgumentException("No valid JSON found in the response");
    }
}
