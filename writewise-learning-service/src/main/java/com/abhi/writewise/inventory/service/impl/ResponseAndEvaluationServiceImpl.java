package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.evaluation.EvaluationResultDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.Evaluation;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationMetric;
import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationResult;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.TopicGenerationRepository;
import com.abhi.writewise.inventory.repository.sql.mysql.WritingSessionRepository;
import com.abhi.writewise.inventory.service.ResponseAndEvaluationService;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.abhi.writewise.inventory.util.RestClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseAndEvaluationServiceImpl implements ResponseAndEvaluationService {
    private final static String LLM_TOPIC = "ollama-llm-writing-module-topics";
    private static final String MODEL_NAME = "llama3";
    private final ResponseMasterRepository responseMasterRepository;
    private final WritingSessionRepository writingSessionRepository;
    private final TopicGenerationRepository topicGenerationRepository;
    private final Integer RETRY_COUNT = 3;
    private final RestClient restClient;


    @Setter
    @Getter
    private String url;


    @Override
    @Transactional
    public ResponseMasterDTO submit(long sqlRefId, long topicRefId, String response) {
        return add(sqlRefId, topicRefId, response, Status.TopicResponse.SUBMITTED);
    }

    @Override
    @Transactional
    public ResponseMasterDTO saveAsDraft(long sqlRefId, long topicRefId, String response) {
        return add(sqlRefId, topicRefId, response, Status.TopicResponse.IN_PROGRESS);
    }

    private ResponseMasterDTO add(long sqlRefId, long topicRefId, String response, int status) {
        WritingSession writingSession = writingSessionRepository.findByRefId(sqlRefId);
        if (writingSession == null) {
            log.error("Unable to find the Writing Session SQL object: {}", sqlRefId);
            throw new ServerException().new EntityObjectNotFound("WritingSession object not found.");
        }
        if (writingSession.getMongoTopicResponseId() == null) {
            log.error("Unable to find the equivalent MongoDB object");
            throw new ServerException().new InternalError("Unable to find the MongoDB object ResponseMaster.");
        }
        ResponseMaster responseMaster = responseMasterRepository.findById(new ObjectId(writingSession.getMongoTopicResponseId())).orElse(null);
        if (responseMaster == null) {
            log.error("Unable to find the ResponseMaster NoSQL object");
            throw new ServerException().new EntityObjectNotFound("ResponseMaster object not found.");
        }
        if (CollectionUtils.isEmpty(responseMaster.getTopicResponseList())) {
            log.error("Unable to find the response list");
            throw new ServerException().new InternalError("ResponseList is not found in the associated master object.");
        }
        Response updatedResponse = responseMaster.getTopicResponseList().stream().filter(res -> (res.getTopic() != null && res.getTopic().getRefId() == topicRefId)).findFirst().orElse(null);
        if (updatedResponse == null) {
            log.error("Unable to find the equivalent Response for the topic given:{}", topicRefId);
            throw new ServerException().new InternalError("Requested response is not found.");
        }
        updatedResponse.setResponse(response);
        updatedResponse.setResponseStatus(status);
        responseMaster = responseMasterRepository.save(responseMaster);
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponseMaster(responseMaster);
    }

    @Override
    @Transactional
    public ResponseMasterDTO evaluate(long sqlRefId, long topicRefId) {
        WritingSession writingSession = writingSessionRepository.findByRefId(sqlRefId);
        if (writingSession == null) {
            log.error("Unable to find the Writing Session SQL object: {}", sqlRefId);
            throw new ServerException().new EntityObjectNotFound("WritingSession object not found.");
        }
        //for fetching the recommendations
        if (writingSession.getMongoTopicId() == null) {
            log.error("Unable to find the TopicGenerationId in WritingSession object");
            throw new ServerException().new InternalError("Unable to find the MongoDB object Mongo TopicId.");
        }
        if (writingSession.getMongoTopicResponseId() == null) {
            log.error("Unable to find the equivalent MongoDB object");
            throw new ServerException().new InternalError("Unable to find the MongoDB object ResponseMaster.");
        }

        TopicGeneration topicGeneration = topicGenerationRepository.findById(new ObjectId(writingSession.getMongoTopicId())).orElse(null);
        if (topicGeneration == null) {
            log.error("The topic generation object is not found in the MongoDB");
            throw new ServerException().new EntityObjectNotFound("Unable to find the Topic Generation object.");
        }

        ResponseMaster responseMaster = responseMasterRepository.findById(new ObjectId(writingSession.getMongoTopicResponseId())).orElse(null);
        if (responseMaster == null) {
            log.error("Unable to find the ResponseMaster NoSQL object");
            throw new ServerException().new EntityObjectNotFound("ResponseMaster object not found.");
        }
        if (CollectionUtils.isEmpty(responseMaster.getTopicResponseList())) {
            log.error("Unable to find the response list");
            throw new ServerException().new InternalError("ResponseList is not found in the associated master object.");
        }
        Response responseEntity = responseMaster.getTopicResponseList().stream().filter(entity -> entity.getTopic().getRefId() == topicRefId).findFirst().orElse(null);
        if (responseEntity == null) {
            log.error("Unable to find the Response entity.{}", topicRefId);
            throw new ServerException().new InternalError("The response entity is not linked with the response master");
        }
        EvaluationResultDTO llmResponse = sendToLlmForEvaluation(responseEntity.getTopic(),
                topicGeneration.getRecommendations(),
                responseEntity.getResponse());
        Evaluation llmEvaluation = updateLlmResponseInEntity(responseEntity, llmResponse);
        responseEntity.setEvaluation(llmEvaluation);
        responseMaster = responseMasterRepository.save(responseMaster);
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponseMaster(responseMaster);
    }

    private Evaluation updateLlmResponseInEntity(Response responseEntity, EvaluationResultDTO llmResponse) {
        Evaluation evaluation = responseEntity.getEvaluation();
        if (evaluation == null) {
            evaluation = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluation();
        }
        double totalScore = 0.0;
        EvaluationResult evaluationResult = evaluation.getEvaluationResult();
        if (evaluationResult == null) {
            evaluationResult = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationResult();
        }
        if (llmResponse.getSpelling() != null) {
            EvaluationMetric spelling = evaluationResult.getSpelling();
            if (spelling == null) {
                spelling = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            spelling.setCategory(ApplicationConstants.EvaluationCategory.SPELLING);
            spelling.setComments(llmResponse.getSpelling().getComments());
            spelling.setAlternateSuggestions(llmResponse.getSpelling().getAlternateSuggestions());
            spelling.setScore(llmResponse.getSpelling().getScore());
            totalScore += (llmResponse.getSpelling().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.SPELLING));
            evaluationResult.setSpelling(spelling);
        }
        if (llmResponse.getGrammar() != null) {
            EvaluationMetric grammar = evaluationResult.getGrammar();
            if (grammar == null) {
                grammar = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            grammar.setCategory(ApplicationConstants.EvaluationCategory.GRAMMAR);
            grammar.setComments(llmResponse.getGrammar().getComments());
            grammar.setAlternateSuggestions(llmResponse.getGrammar().getAlternateSuggestions());
            grammar.setScore(llmResponse.getGrammar().getScore());
            totalScore += (llmResponse.getGrammar().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.GRAMMAR));
            evaluationResult.setGrammar(grammar);
        }
        if (llmResponse.getPunctuation() != null) {
            EvaluationMetric punctuation = evaluationResult.getPunctuation();
            if (punctuation == null) {
                punctuation = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            punctuation.setCategory(ApplicationConstants.EvaluationCategory.PUNCTUATION);
            punctuation.setComments(llmResponse.getPunctuation().getComments());
            punctuation.setAlternateSuggestions(llmResponse.getPunctuation().getAlternateSuggestions());
            punctuation.setScore(llmResponse.getPunctuation().getScore());
            totalScore += (llmResponse.getPunctuation().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.PUNCTUATION));
            evaluationResult.setPunctuation(punctuation);
        }
        if (llmResponse.getVocabulary() != null) {
            EvaluationMetric vocabulary = evaluationResult.getVocabulary();
            if (vocabulary == null) {
                vocabulary = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            vocabulary.setCategory(ApplicationConstants.EvaluationCategory.VOCABULARY);
            vocabulary.setComments(llmResponse.getVocabulary().getComments());
            vocabulary.setAlternateSuggestions(llmResponse.getVocabulary().getAlternateSuggestions());
            vocabulary.setScore(llmResponse.getVocabulary().getScore());
            totalScore += (llmResponse.getVocabulary().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.VOCABULARY));
            evaluationResult.setVocabulary(vocabulary);
        }
        if (llmResponse.getStyleAndTone() != null) {
            EvaluationMetric styleAndTone = evaluationResult.getStyleAndTone();
            if (styleAndTone == null) {
                styleAndTone = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            styleAndTone.setCategory(ApplicationConstants.EvaluationCategory.STYLE_AND_TONE);
            styleAndTone.setAlternateSuggestions(llmResponse.getStyleAndTone().getAlternateSuggestions());
            styleAndTone.setScore(llmResponse.getStyleAndTone().getScore());
            totalScore += (llmResponse.getStyleAndTone().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.STYLE_AND_TONE));
            evaluationResult.setVocabulary(styleAndTone);
        }
        if (llmResponse.getCreativityAndThinking() != null) {
            EvaluationMetric creativityAndThinking = evaluationResult.getCreativityAndThinking();
            if (creativityAndThinking == null) {
                creativityAndThinking = TopicResponseEvalServiceUtil.EvaluationUtil.BuildEntity.buildEvaluationMetric();
            }
            creativityAndThinking.setCategory(ApplicationConstants.EvaluationCategory.CREATIVITY_AND_THINKING);
            creativityAndThinking.setAlternateSuggestions(llmResponse.getCreativityAndThinking().getAlternateSuggestions());
            creativityAndThinking.setScore(llmResponse.getCreativityAndThinking().getScore());
            totalScore += (llmResponse.getCreativityAndThinking().getScore() * ApplicationConstants.EvaluationCategory.getWeight(ApplicationConstants.EvaluationCategory.CREATIVITY_AND_THINKING));
            evaluationResult.setCreativityAndThinking(creativityAndThinking);
        }
        evaluationResult.setScore(totalScore);
        evaluation.setEvaluationResult(evaluationResult);
        evaluation.setEvaluationStatus(Status.EvaluationStatus.COMPLETED);
        evaluation.setLastUpdDate(LocalDateTime.now());
        return evaluation;
    }

    private EvaluationResultDTO sendToLlmForEvaluation(Topic topicEntity, List<String> recommendations, String userResponse) {
        StringBuilder pointsStr = new StringBuilder();
        for (String point : topicEntity.getPoints()) {
            pointsStr.append("\"").append(point).append("\"").append(",");
        }
        StringBuilder recommendationsStr = new StringBuilder();
        for (String recommendation : recommendations) {
            recommendationsStr.append("\"").append(recommendation).append("\"").append(",");
        }
        loadModelServiceName();
        String prompt = LLMPromptBuilder.EvaluationPrompt.prompt(topicEntity.getTopic(), topicEntity.getSubject(), pointsStr.substring(0, pointsStr.length() - 1), recommendationsStr.substring(0, recommendationsStr.length() - 1), userResponse);
        PromptRequest request = new PromptRequest(prompt);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<String> responseEntity;
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
            setUrl(properties.getProperty(LLM_TOPIC) + MODEL_NAME);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    private String extractJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("<Response>(.*?)</Response>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        throw new IllegalArgumentException("No valid JSON found in the response");
    }

    private EvaluationResultDTO mapLlmResponseToObject(String response) {
        try {
            String json = extractJsonFromResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, EvaluationResultDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private record PromptRequest(
            @NotBlank @JsonProperty("prompt") String prompt
    ) {
    }
}
