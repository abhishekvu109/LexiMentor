package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.TopicGenerationRepository;
import com.abhi.writewise.inventory.repository.sql.mysql.WritingSessionRepository;
import com.abhi.writewise.inventory.service.TopicService;
import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.abhi.writewise.inventory.util.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicServiceImpl implements TopicService {
    private final static String LLM_TOPIC = "ollama-llm-writing-module-topics";
    private static final String MODEL_NAME = "llama3";
    private final WritingSessionRepository writingSessionRepository;
    private final RestClient restClient;
    private final Integer RETRY_COUNT = 3;
    private final MongoTemplate mongoTemplate;
    private final TopicGenerationRepository topicGenerationRepository;
    private final ResponseMasterRepository responseMasterRepository;
    private String url;

    @Override
    public TopicGenerationDTO addTopicGenerationsUsingLLM(TopicGenerationDTO request) {
        log.info("LLM topic generation service is called.");
        WritingSession sqlEntity = WritingSession.builder().refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).deleteInd(Status.Topic.DeleteStatus.ACTIVE).status(Status.Topic.TOPIC_REQUEST).build();
        long refId = sqlEntity.getRefId();
        sqlEntity = writingSessionRepository.save(sqlEntity);
        log.info("A new record has been persisted: {}", sqlEntity);
        loadModelServiceName();
        String prompt = (StringUtils.isEmpty(request.getPrompt())) ? LLMPromptBuilder.TopicPrompt.prompt(request.getSubject(), request.getNumOfTopic(), request.getPurpose(), request.getWordCount()) : request.getPrompt();
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
                log.info("The llm service service has returned a response : {}", responseEntity);
                break;
            } catch (Exception ex) {
                log.error("Unable to get response from the llm service {} for {}", LLM_TOPIC, request);
                log.error(ex.getMessage());
                log.info("Attempting retry : {}", (RETRY_COUNT - retry));
                retry--;
            }
        }
        TopicGenerationDTO response = mapLlmResponseToObject(responseOutput);
        log.info("LLM has generated the response. {}", response);
        if (response != null) {
            response.setPrompt(prompt);
            response.setSubject(request.getSubject());
            response.setPurpose(request.getPurpose());
            response.setWordCount(request.getWordCount());
            response.setNumOfTopic(request.getNumOfTopic());
            TopicGeneration topicGenerationEntity = TopicResponseEvalServiceUtil.TopicUtil.buildEntity(response);
//            topicGenerationEntity = mongoTemplate.insert(topicGenerationEntity);
            topicGenerationEntity = topicGenerationRepository.save(topicGenerationEntity);
            log.info("LLM response has been saved in mongo: {}", topicGenerationEntity);
            ResponseMaster responseMaster = buildResponseEntity(topicGenerationEntity, refId);
            responseMaster = responseMasterRepository.save(responseMaster);
            ObjectId mongoTopicId = topicGenerationEntity.getId();
            ObjectId responseMasterId = responseMaster.getId();
            CompletableFuture.runAsync(() -> {
                WritingSession dbEntity = writingSessionRepository.findByRefId(refId);
                dbEntity.setStatus(Status.Topic.TOPIC_RESPONSE);
                dbEntity.setMongoTopicId(mongoTopicId.toHexString());
                dbEntity.setMongoTopicResponseId(responseMasterId.toHexString());
                dbEntity = writingSessionRepository.save(dbEntity);
                log.info("The SQL record status is changed to {}", Status.Topic.getMessage(Status.Topic.TOPIC_RESPONSE));
                log.info("Mongo object: {}", dbEntity);
            });
        }
        return response;
    }

    private ResponseMaster buildResponseEntity(TopicGeneration topicGeneration, long sqlRefId) {
        ResponseMaster responseMaster = TopicResponseEvalServiceUtil.ResponseUtil.BuildEntity.buildResponseMaster();
        List<Response> responses = new LinkedList<>();
        topicGeneration.getTopics().forEach(topic -> {
            Response response = TopicResponseEvalServiceUtil.ResponseUtil.BuildEntity.buildResponse();
            response.setTopic(topic);
            responses.add(response);
        });
        responseMaster.setTopicResponseList(responses);
        responseMaster.setSqlRefId(sqlRefId);
        return responseMaster;
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

    private TopicGenerationDTO mapLlmResponseToObject(String response) {
        try {
            String json = extractJsonFromResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, TopicGenerationDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<TopicGenerationDTO> findAllTopicGenerations() {
        return writingSessionRepository.findAll().stream().filter(writingSession -> StringUtils.isNotEmpty(writingSession.getMongoTopicId())).toList().stream().map(sqlEntity -> TopicResponseEvalServiceUtil.TopicUtil.buildLlmTopicDTO(sqlEntity, Objects.requireNonNull(mongoTemplate.findById(sqlEntity.getMongoTopicId(), TopicGeneration.class)))).toList();
    }

    @Override
    public TopicGenerationDTO findTopicGenerationByRefId(long refId) {
        WritingSession sqlLlmEntity = writingSessionRepository.findByRefId(refId);
        TopicGeneration noSqlLlmEntity = mongoTemplate.findById(sqlLlmEntity.getMongoTopicId(), TopicGeneration.class);
        if (noSqlLlmEntity == null)
            throw new ServerException().new InternalError("Unable to find the equivalent Mongo DB instance.");
        return TopicResponseEvalServiceUtil.TopicUtil.buildLlmTopicDTO(sqlLlmEntity, noSqlLlmEntity);
    }

    @Override
    @Transactional
    public void removeTopicGenerationByRefId(long refId) {
        WritingSession writingSession = writingSessionRepository.findByRefId(refId);
        if (StringUtils.isNotEmpty(writingSession.getMongoTopicId())) {
            TopicGeneration topicGeneration = topicGenerationRepository.findById(new ObjectId(writingSession.getMongoTopicId())).orElse(null);
            if (topicGeneration != null) topicGenerationRepository.delete(topicGeneration);
        }
        if (StringUtils.isNotEmpty(writingSession.getMongoTopicResponseId())) {
            ResponseMaster responseMaster = responseMasterRepository.findById(new ObjectId(writingSession.getMongoTopicResponseId())).orElse(null);
            if (responseMaster != null) responseMasterRepository.delete(responseMaster);
        }
        writingSessionRepository.delete(writingSession);
    }

    @Override
    @Transactional
    public void removeAllTopicGenerations() {
        List<WritingSession> writingSessions = writingSessionRepository.findAll();
        writingSessions.forEach(ws -> {
            removeTopicGenerationByRefId(ws.getRefId());
        });
    }

    @Override
    public List<TopicDTO> findAllTopics() {
        List<TopicGeneration> topicGenerations = topicGenerationRepository.findAll();
        List<TopicDTO> topicDTOS = new LinkedList<>();
        topicGenerations.forEach(topicGeneration -> {
            List<TopicDTO> dtos = CollectionUtils.isNotEmpty(topicGeneration.getTopics()) ? topicGeneration.getTopics().stream().map(TopicResponseEvalServiceUtil.TopicUtil::buildTopicDTO).toList() : Collections.emptyList();
            if (CollectionUtils.isNotEmpty(dtos)) {
                dtos.forEach(dto -> {
                    dto.setRecommendations(topicGeneration.getRecommendations());
                });
                topicDTOS.addAll(dtos);
            }
        });
        return topicDTOS;
    }

    @Override
    public TopicDTO findTopicByRefId(long refId) {
        List<TopicGeneration> topicGenerations = topicGenerationRepository.findAll();
        TopicGeneration tg = null;
        Topic topic = null;
        for (TopicGeneration topicGeneration : topicGenerations) {
            if (CollectionUtils.isNotEmpty(topicGeneration.getTopics())) {
                topic = topicGeneration.getTopics().stream().filter(topicEntity -> topicEntity.getRefId() == refId).findAny().orElse(null);
                if(topic!=null){
                    tg = topicGeneration;
                    break;
                }
            }
        }
        if (tg != null) {
            TopicDTO topicDTO = TopicResponseEvalServiceUtil.TopicUtil.buildTopicDTO(topic);
            topicDTO.setRecommendations(tg.getRecommendations());
            return topicDTO;
        } else {
            throw new ServerException().new InternalError("TopicDTO can't be built.");
        }
    }

    @Override
    public void removeTopicByRefId(long refId) {

    }
}
