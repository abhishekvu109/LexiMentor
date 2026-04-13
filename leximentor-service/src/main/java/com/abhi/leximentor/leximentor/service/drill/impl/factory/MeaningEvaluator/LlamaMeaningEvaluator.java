package com.abhi.leximentor.leximentor.service.drill.impl.factory.MeaningEvaluator;

import com.abhi.leximentor.leximentor.dto.other.LlamaModelDTO;
import com.abhi.leximentor.leximentor.service.drill.impl.factory.MeaningEvaluatorFactory;
import com.abhi.leximentor.leximentor.util.RestClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Properties;

@Data
@Slf4j
@RequiredArgsConstructor
public class LlamaMeaningEvaluator implements MeaningEvaluatorFactory {
    private final static String EVALUATOR = "llama-llm-based-evaluator";
    private final RestClient restClient;
    private String url;

    @Override
    public LlamaModelDTO response(String prompt, int retryCount) {
        int RETRY_COUNT = retryCount;
        loadModelServiceName();
        LlamaModelDTO request = LlamaModelDTO.builder().explanation("").confidence(0).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<LlamaModelDTO> responseEntity = null;
        LlamaModelDTO llamaModelDTO = null;
        while (RETRY_COUNT > 0) {
            try {
                responseEntity = restClient.post(url, headers, request, LlamaModelDTO.class);
                llamaModelDTO = responseEntity.getBody();
                log.info("The Llama evaluator service has returned a response : {}", responseEntity);
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
        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource("application.yaml"));
        Properties properties = yamlFactory.getObject();
        if (properties == null) {
            log.error("Unable to load configuration from application.yaml");
            return;
        }
        log.info("Successfully found the evaluator address: {}", properties.getProperty(EVALUATOR));
        setUrl(properties.getProperty(EVALUATOR));
    }
}
