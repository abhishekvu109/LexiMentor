package com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluator;

import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.service.drill.impl.factory.MeaningEvaluatorFactory;
import com.abhi.leximentor.inventory.util.RestClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Properties;

@Data
@Slf4j
@RequiredArgsConstructor
public class OllamaMeaningEvaluator implements MeaningEvaluatorFactory {
    private final static String EVALUATOR = "ollama-llm-based-evaluator";
    private final RestClient restClient;
    private String url;

    @Override
    public LlamaModelDTO response(String prompt, int retryCount) {
        int RETRY_COUNT = retryCount;
        loadModelServiceName();
        LlamaModelDTO request = LlamaModelDTO.builder().text(prompt).explanation("").confidence(0).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<LlamaModelDTO> responseEntity = null;
        LlamaModelDTO llamaModelDTO = null;
        while (RETRY_COUNT > 0) {
            try {
                responseEntity = restClient.post(url, headers, request, LlamaModelDTO.class);
                llamaModelDTO = responseEntity.getBody();
                log.info("The Ollama evaluator service has returned a response : {}", responseEntity);
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
            setUrl(properties.getProperty(EVALUATOR) + "ollama-llm-based-evaluator");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
