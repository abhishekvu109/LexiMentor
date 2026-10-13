package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.model.PromptRequest;
import com.abhi.writewise.inventory.model.PromptResponse;
import com.abhi.writewise.inventory.service.LLMService;
import com.abhi.writewise.inventory.util.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMServiceImpl implements LLMService {
    private final RestClient restClient;
    @Value("${ollama-llm-writing-module-topics}")
    private String LLM_TOPIC;

    @Override
    public PromptResponse execute(PromptRequest request) {
        validate(request);
        String URL = LLM_TOPIC;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<String> responseEntity;
        String responseOutput;
        try {
            responseEntity = restClient.post(URL, headers, request, String.class);
            responseOutput = responseEntity.getBody();
            if (StringUtils.isEmpty(responseOutput)) {
                log.error("LLM service returned empty response body");
                throw new ServerException().new InternalError("LLM service returned empty response");
            }
            log.info("The llm service service has returned a response : {}", responseEntity);
        } catch (Exception ex) {
            log.error("Unable to get response from the llm service {} for {}", URL, request);
            log.error(ex.getMessage());
            throw new ServerException().new InternalError(ex.getMessage());
        }
        return PromptResponse.builder().response(responseOutput).build();
    }

    private void validate(PromptRequest request) {
        String validationMessage = "";
        if (StringUtils.isEmpty(request.getPrompt())) {
            validationMessage = "Prompt is empty.";
        } else if (StringUtils.isEmpty(request.getModel())) {
            validationMessage = "Model is empty.";
        }
        if (StringUtils.isNotEmpty(validationMessage)) {
            throw new ServerException().new InternalError("Invalid prompt request :" + validationMessage);
        }
    }
}
