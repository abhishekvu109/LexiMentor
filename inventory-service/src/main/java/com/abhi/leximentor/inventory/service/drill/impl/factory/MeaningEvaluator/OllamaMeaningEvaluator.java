package com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluator;

import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluatorFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@RequiredArgsConstructor
public class OllamaMeaningEvaluator implements MeaningEvaluatorFactory {
    private final static String EVALUATOR = "ollama-llm-based-evaluator";
    private final RestTemplate restTemplate;
    private String url;

    @Override
    public LlamaModelDTO response(String prompt, int retryCount) {
        int RETRY_COUNT = retryCount;
        loadModelServiceName();
        LlamaModelDTO request = LlamaModelDTO.builder().text(prompt).explanation("").confidence(0).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<String> stringResponseEntity = null;
        LlamaModelDTO llamaModelDTO = null;
        while (RETRY_COUNT > 0) {
            try {
                stringResponseEntity = post(url, headers, request);
                String strRes = stringResponseEntity.getBody();
                llamaModelDTO = parseJsonString(extractJsonString(strRes));
                log.info("The Ollama evaluator service has returned a response : {}", stringResponseEntity);
                break;
            } catch (Exception ex) {
                log.error("Unable to get response from the evaluator {} for {}", EVALUATOR, request);
                log.error(ex.getMessage());
                log.info("Attempting retry : {}", (retryCount - RETRY_COUNT) + 1);
                RETRY_COUNT--;
            }
        }
        return llamaModelDTO;
    }

    private void loadModelServiceName() {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
            log.info("Successfully found the evaluator address: {}", properties.getProperty(EVALUATOR));
            setUrl(properties.getProperty(EVALUATOR));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private ResponseEntity<String> post(String url, HttpHeaders headers, LlamaModelDTO requestBody) {
        HttpEntity<LlamaModelDTO> entity = new HttpEntity<>(requestBody, headers);
        log.info("The request before sending:{}", entity);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    private String extractJsonString(String input) {
        Pattern pattern = Pattern.compile("\\{.*?\\}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "{}";
    }

    private static LlamaModelDTO parseJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        LlamaModelDTO llamaModelDTO;
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (rootNode.has("confidence") && rootNode.has("explanation") && rootNode.has("isCorrect")) {
                llamaModelDTO = objectMapper.readValue(jsonString, LlamaModelDTO.class);
            } else {
                llamaModelDTO = LlamaModelDTO.getDefaultInstance();
            }
        } catch (Exception e) {
            llamaModelDTO = LlamaModelDTO.getDefaultInstance();
            e.printStackTrace();
            log.error("Failed to evaluate the response of the user {}", e.getMessage());
        }
        return llamaModelDTO;
    }
}
