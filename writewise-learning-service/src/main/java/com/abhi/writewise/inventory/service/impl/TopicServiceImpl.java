package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.dto.LlmTopicDTO;
import com.abhi.writewise.inventory.service.TopicService;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.abhi.writewise.inventory.util.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class TopicServiceImpl implements TopicService {
    private final static String LLM_TOPIC = "ollama-llm-writing-module-topics";
    private final RestClient restClient;
    private String url;
    private final Integer RETRY_COUNT = 3;

    @Override
    public LlmTopicDTO generateTopicsFromLlm(LlmTopicDTO request) {
        loadModelServiceName();
        String prompt = (StringUtils.isEmpty(request.getPrompt())) ? LLMPromptBuilder.TopicPrompt.prompt(request.getSubject(), request.getNumOfTopic(), request.getExam()) : request.getPrompt();
        request.setPrompt(prompt);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<String> responseEntity = null;
        String responseOutput = null;
        int retry = RETRY_COUNT;
        while (retry > 0) {
            try {
                responseEntity = restClient.post(url, headers, request, String.class);
                responseOutput = responseEntity.getBody();
                log.info("The llm service service has returned a response : {}", responseEntity);
                break;
            } catch (Exception ex) {
                log.error("Unable to get response from the llm service {} for {}", LLM_TOPIC, request);
                log.error(ex.getMessage());
                log.info("Attempting retry : {}", (RETRY_COUNT - retry));
                retry--;
            }
        }
        LlmTopicDTO response=mapLlmResponseToObject(responseOutput);
        if(response!=null){
            response.setPrompt(prompt);
        }
        return response;
    }

    private void loadModelServiceName() {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
            log.info("Successfully found the llm topic address: {}", properties.getProperty(LLM_TOPIC));
            setUrl(properties.getProperty(LLM_TOPIC));
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

    private LlmTopicDTO mapLlmResponseToObject(String response) {
        try {
            String json = extractJsonFromResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, LlmTopicDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
