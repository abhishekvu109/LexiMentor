package com.abhi.leximentor.inventory;

import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;
import com.abhi.leximentor.inventory.util.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestClass {
    public RestClient restClient;
    public String url;

    public TestClass() {
        this.url = "http://localhost:6565/api/v1/llm/evaluation/meaning/ollama-llm-based-evaluator";
        this.restClient = new RestClient(new RestTemplate());
    }

    public LlamaModelDTO response(String prompt, int retryCount) {
        int RETRY_COUNT = retryCount;
        LlamaModelDTO request = LlamaModelDTO.builder().text(prompt).explanation("").confidence(0).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<LlamaModelDTO> responseEntity = null;
        LlamaModelDTO llamaModelDTO = null;
        while (RETRY_COUNT > 0) {
            try {
                responseEntity = restClient.post(url, headers, request, LlamaModelDTO.class);
                llamaModelDTO = responseEntity.getBody();
                break;
            } catch (Exception ex) {
                RETRY_COUNT--;
                System.out.println(ex.getMessage());
            }
        }
        return llamaModelDTO;
    }

    public static void main(String[] args) {
        String prompt = "Consider you are evaluating a student's understanding of vocabulary. Your task is to assess if the student has correctly defined a given word. You will receive the word itself, its part of speech, the official definition, and the student's interpretation. Please provide your evaluation in a JSON format as follows: {\\\"isCorrect\\\": true, \\\"confidence\\\": 0, \\\"explanation\\\": \\\"Explanation of your decision\\\"} Here's the scenario: word: uxorial Official Definition: befitting or characteristic of a wife Student's response: Wifey characteristics. Evaluate the student's response based on the provided information. Assign true to isCorrect if the student's definition accurately reflects the meaning of the word. Assign false otherwise. Use the confidence score to indicate how certain you are about your judgment (0 for least confident, 100 for most confident). Provide an explanation to justify your decision.";
        TestClass testClass = new TestClass();
        LlamaModelDTO llamaModelDTO = testClass.response(prompt, 1);
        System.out.println(llamaModelDTO);
    }
}
