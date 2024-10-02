package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.dto.other.LlmWritingTopicDTO;
import com.abhi.leximentor.inventory.service.inv.WritingModuleService;
import com.abhi.leximentor.inventory.util.LLMPromptBuilder;
import com.abhi.leximentor.inventory.util.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class WritingModuleServiceImpl implements WritingModuleService {
    private final static String LLM_TOPIC = "ollama-llm-writing-module-topics";
    private final RestClient restClient;
    private String url;
    private final Integer RETRY_COUNT = 3;

    @Override
    public LlmWritingTopicDTO getTopics(LlmWritingTopicDTO request) {
        loadModelServiceName();
        String prompt = LLMPromptBuilder.WritingModule.getTopicsPrompt(request.getSubject(), request.getNumOfTopic(), request.getExam());
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
        return mapLlmResponseToObject(responseOutput);
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

    private LlmWritingTopicDTO mapLlmResponseToObject(String response) {
        try {
            String json = extractJsonFromResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, LlmWritingTopicDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

//    private static String extractJsonFromResponse1(String response) {
//        Pattern pattern = Pattern.compile("<response>(.*?)</response>", Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(response);
//        if (matcher.find()) {
//            return matcher.group(1).trim();
//        }
//        throw new IllegalArgumentException("No valid JSON found in the response");
//    }
//
//    private static LlmWritingTopicDTO mapLlmResponseToObject1(String response) {
//        try {
//            String json = extractJsonFromResponse1(response);
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.readValue(json, LlmWritingTopicDTO.class);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return null;
//        }
//    }
//
//    public static void main(String[] args) {
//        String abc= """
//                <response>
//                {
//                  "topics": [
//                    {
//                      "topicNo": 1,
//                      "topic": "The Role of Free Markets in Economic Development",
//                      "subject": "global economy, economic development",
//                      "description": ["In this topic, you can discuss the role of free markets in stimulating economic growth and development. You could argue whether government intervention is necessary to ensure equitable distribution of resources or if market forces are sufficient. Some possible points to consider include"],
//                      "points": [
//                        "Advantages of free markets, such as increased competition and innovation",
//                        "Challenges faced by developing economies, including limited infrastructure and lack of resources",
//                        "Role of government in regulating markets, including taxation and public spending"
//                      ],
//                      "learning": "This topic allows you to demonstrate your understanding of economic systems, market forces, and the role of government in shaping economic development."
//                    },
//                    {
//                      "topicNo": 2,
//                      "topic": "The Impact of Automation on Job Creation and Unemployment",
//                      "subject": "macroeconomics, labor market",
//                      "description": ["In this topic, you can analyze the effects of automation on job creation and unemployment. You could discuss whether automation is a net positive or negative for the economy, considering factors such as"],
//                      "points": [
//                        "Job displacement and the need for retraining or upskilling",
//                        "Increased productivity and efficiency gains",
//                        "Impact on industries with high automation rates, such as manufacturing"
//                      ],
//                      "learning": "This topic enables you to showcase your knowledge of labor market trends, technological advancements, and the challenges of adapting to changing economic conditions."
//                    },
//                    {
//                      "topicNo": 3,
//                      "topic": "The Effectiveness of Sustainable Development Goals (SDGs) in Addressing Global Challenges",
//                      "subject": "international development, sustainability",
//                      "description": ["In this topic, you can evaluate the effectiveness of the SDGs in addressing pressing global challenges. You could discuss whether the goals are achievable and what steps governments, businesses, and individuals can take to support their implementation. Some possible points to consider include"],
//                      "points": [
//                        "Goals such as poverty reduction, climate action, and gender equality",
//                        "Challenges faced by developing economies in achieving the SDGs",
//                        "Role of international cooperation and diplomacy in supporting sustainable development"
//                      ],
//                      "learning": "This topic allows you to demonstrate your understanding of global challenges, sustainable development, and the role of international cooperation in addressing pressing issues."
//                    }
//                  ],
//                  "recommendations": [
//                    "Read widely on the subject to gather information and ideas.",
//                    "Structure your essay with a clear introduction, body paragraphs, and conclusion.",
//                    "Use economic terminology accurately and consistently.",
//                    "Provide specific examples or case studies to support your arguments.",
//                    "Practice writing under timed conditions to simulate the IELTS exam experience."
//                  ]
//                }
//                </response>
//                """;
//        LlmWritingTopicDTO llmWritingTopicDTO=mapLlmResponseToObject1(abc);
//        System.out.println(llmWritingTopicDTO);
//    }
}
