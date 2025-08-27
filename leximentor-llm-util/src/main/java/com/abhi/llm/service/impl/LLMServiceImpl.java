package com.abhi.llm.service.impl;

import com.abhi.llm.constants.ModelConstant;
import com.abhi.llm.model.PromptRequest;
import com.abhi.llm.model.PromptResponse;
import com.abhi.llm.service.LLMService;
import com.abhi.llm.util.RestClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class LLMServiceImpl implements LLMService {
    private final RestClient restClient = new RestClient();
    private String LLM_TOPIC;

    public LLMServiceImpl(String LLM_TOPIC) {
        this.LLM_TOPIC = LLM_TOPIC;
    }

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
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        PromptResponse promptResponse=new PromptResponse();
        promptResponse.setResponse(responseOutput);
        return promptResponse;
    }

    private void validate(PromptRequest request) {
        if (StringUtils.isEmpty(LLM_TOPIC)) {
            throw new RuntimeException("LLM URL is empty");
        }
        if (request.getOptions() == null) {
            request.setOptions(ModelConstant.DEFAULT_OLLAMA_OPTIONS);
        }
        String validationMessage = "";
        if (StringUtils.isEmpty(request.getPrompt())) {
            validationMessage = "Prompt is empty.";
        } else if (StringUtils.isEmpty(request.getFormat())) {
            validationMessage = "Response format is empty.";
        } else if (StringUtils.isEmpty(request.getModel())) {
            validationMessage = "Model is empty";
        }
        if (StringUtils.isNotEmpty(validationMessage)) {
            throw new RuntimeException("Invalid prompt request :" + validationMessage);
        }
    }

    @Override
    public void setURL(String URL) {
        this.LLM_TOPIC = URL;
    }
}
