package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.ModelConstants;
import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.topic.CreateTopicManuallyDTO;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.model.PromptRequest;
import com.abhi.writewise.inventory.model.PromptResponse;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.TopicGenerationRepository;
import com.abhi.writewise.inventory.repository.sql.mysql.WritingSessionRepository;
import com.abhi.writewise.inventory.service.LLMService;
import com.abhi.writewise.inventory.service.TopicService;
import com.abhi.writewise.inventory.util.KeyGeneratorUtil;
import com.abhi.writewise.inventory.util.LLMPromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicServiceImpl implements TopicService {

    private final WritingSessionRepository writingSessionRepository;
    private final LLMService llmService;
    private final MongoTemplate mongoTemplate;
    private final TopicGenerationRepository topicGenerationRepository;
    private final ResponseMasterRepository responseMasterRepository;

    private static final int RETRY_COUNT = 3;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public TopicGenerationDTO addTopicGenerationsUsingLLM(TopicGenerationDTO request) {
        log.info("Cloud LLM topic generation service is called.");
        WritingSession sqlEntity = WritingSession.builder()
                .refId(KeyGeneratorUtil.refId())
                .uuid(KeyGeneratorUtil.uuid())
                .deleteInd(Status.Topic.DeleteStatus.ACTIVE)
                .status(Status.Topic.TOPIC_REQUEST)
                .build();
        long refId = sqlEntity.getRefId();
        sqlEntity = writingSessionRepository.save(sqlEntity);
        log.info("A new writing session record has been persisted: {}", sqlEntity);

        String prompt = StringUtils.isEmpty(request.getPrompt())
                ? LLMPromptBuilder.TopicPrompt.prompt(request.getSubject(), request.getNumOfTopic(), request.getPurpose(), request.getWordCount())
                : request.getPrompt();
        request.setPrompt(prompt);

        PromptRequest promptRequest = PromptRequest.builder()
                .prompt(prompt)
                .model(ModelConstants.CLOUD_LLM)
                .options(ApplicationConstants.DEFAULT_OLLAMA_OPTIONS)
                .build();

        String responseOutput = null;
        int retry = RETRY_COUNT;
        while (retry > 0) {
            try {
                PromptResponse promptResponse = llmService.execute(promptRequest);
                responseOutput = promptResponse.getResponse();
                log.info("Cloud LLM returned a topic generation response.");
                break;
            } catch (Exception ex) {
                log.error("Cloud LLM call failed (attempt {}): {}", RETRY_COUNT - retry + 1, ex.getMessage());
                retry--;
            }
        }

        TopicGenerationDTO response = mapLlmResponseToObject(responseOutput);
        log.info("LLM topic response mapped: {}", response);

        if (response != null) {
            response.setPrompt(prompt);
            response.setSubject(request.getSubject());
            response.setPurpose(request.getPurpose());
            response.setWordCount(request.getWordCount());
            response.setNumOfTopic(request.getNumOfTopic());
            TopicGeneration topicGenerationEntity = TopicResponseEvalServiceUtil.TopicUtil.buildEntity(response);
            topicGenerationEntity = topicGenerationRepository.save(topicGenerationEntity);
            log.info("LLM response saved to MongoDB: {}", topicGenerationEntity);
            ResponseMaster responseMaster = buildResponseEntity(topicGenerationEntity, refId);
            responseMaster = responseMasterRepository.save(responseMaster);
            ObjectId mongoTopicId = topicGenerationEntity.getId();
            ObjectId responseMasterId = responseMaster.getId();
            CompletableFuture.runAsync(() -> {
                WritingSession dbEntity = writingSessionRepository.findByRefId(refId);
                dbEntity.setStatus(Status.Topic.TOPIC_RESPONSE);
                dbEntity.setMongoTopicId(mongoTopicId.toHexString());
                dbEntity.setMongoTopicResponseId(responseMasterId.toHexString());
                writingSessionRepository.save(dbEntity);
                log.info("WritingSession status updated to TOPIC_RESPONSE.");
            });
        }
        return response;
    }

    @Override
    @Transactional
    public TopicGenerationDTO addTopicManually(CreateTopicManuallyDTO request) {
        log.info("Manual topic creation service is called.");
        WritingSession sqlEntity = WritingSession.builder()
                .refId(KeyGeneratorUtil.refId())
                .uuid(KeyGeneratorUtil.uuid())
                .deleteInd(Status.Topic.DeleteStatus.ACTIVE)
                .status(Status.Topic.TOPIC_REQUEST)
                .build();
        long sqlRefId = sqlEntity.getRefId();
        sqlEntity = writingSessionRepository.save(sqlEntity);
        log.info("A new writing session record has been persisted for manual topic: {}", sqlEntity);

        // Create a single Topic from the request
        Topic topic = Topic.builder()
                .refId(KeyGeneratorUtil.refId())
                .uuid(KeyGeneratorUtil.uuid())
                .topicNo(1)
                .topic(request.getTopic())
                .subject(request.getSubject())
                .description(request.getDescription())
                .points(CollectionUtils.isNotEmpty(request.getPoints()) ? request.getPoints() : Collections.emptyList())
                .learning(request.getLearning())
                .build();

        // Create TopicGeneration to group the topic with recommendations
        TopicGeneration topicGeneration = TopicGeneration.builder()
                .refId(KeyGeneratorUtil.refId())
                .uuid(KeyGeneratorUtil.uuid())
                .subject(request.getSubject())
                .numOfTopic(1)
                .purpose("Manual Topic Creation")
                .wordCount(request.getWordCount() > 0 ? request.getWordCount() : 1000)
                .prompt("Manual topic creation")
                .topics(Collections.singletonList(topic))
                .recommendations(CollectionUtils.isNotEmpty(request.getRecommendations()) ? request.getRecommendations() : Collections.emptyList())
                .build();

        topicGeneration = topicGenerationRepository.save(topicGeneration);
        log.info("Manual topic saved to MongoDB: {}", topicGeneration);

        // Create Response entities for writing
        ResponseMaster responseMaster = buildResponseEntity(topicGeneration, sqlRefId);
        responseMaster = responseMasterRepository.save(responseMaster);
        ObjectId mongoTopicId = topicGeneration.getId();
        ObjectId responseMasterId = responseMaster.getId();

        // Update WritingSession synchronously (not async) to ensure it's persisted before returning
        sqlEntity.setStatus(Status.Topic.TOPIC_RESPONSE);
        sqlEntity.setMongoTopicId(mongoTopicId.toHexString());
        sqlEntity.setMongoTopicResponseId(responseMasterId.toHexString());
        sqlEntity = writingSessionRepository.save(sqlEntity);
        log.info("WritingSession status updated to TOPIC_RESPONSE for manual topic.");

        // Build and return the DTO
        TopicGenerationDTO response = TopicResponseEvalServiceUtil.TopicUtil.buildDTO(sqlEntity, topicGeneration);
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

    private TopicGenerationDTO mapLlmResponseToObject(String response) {
        try {
            String unwrapped = unwrapIfJsonEncoded(response);
            String json = extractJsonFromResponse(unwrapped);
            return MAPPER.readValue(json, TopicGenerationDTO.class);
        } catch (Exception e) {
            log.error("Failed to map LLM response to TopicGenerationDTO: {}", e.getMessage());
            log.error("Raw response was: {}", response);
            return null;
        }
    }

    // FastAPI returns Python str as a JSON-encoded string — unwrap it if needed
    private String unwrapIfJsonEncoded(String raw) {
        if (StringUtils.isBlank(raw)) return raw;
        String s = raw.trim();
        if (!s.startsWith("\"")) return s;
        try {
            return MAPPER.readValue(s, String.class);
        } catch (Exception e) {
            if (s.endsWith("\"")) s = s.substring(1, s.length() - 1);
            else s = s.substring(1);
            return s.replace("\\n", "\n").replace("\\r", "").replace("\\\"", "\"").replace("\\\\", "\\");
        }
    }

    private String extractJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("<response>(.*?)</response>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        // Fallback: if no tags, try to use the whole response as JSON
        log.warn("No <response> tags found in LLM output, attempting to parse raw response as JSON.");
        return response.trim();
    }

    @Override
    public List<TopicGenerationDTO> findAllTopicGenerations() {
        return writingSessionRepository.findAll().stream()
                .filter(ws -> StringUtils.isNotEmpty(ws.getMongoTopicId()))
                .map(sqlEntity -> TopicResponseEvalServiceUtil.TopicUtil.buildDTO(
                        sqlEntity,
                        Objects.requireNonNull(mongoTemplate.findById(sqlEntity.getMongoTopicId(), TopicGeneration.class))))
                .toList();
    }

    @Override
    public TopicGenerationDTO findTopicGenerationByRefId(long refId) {
        TopicGeneration noSqlLlmEntity = topicGenerationRepository.findByRefId(refId);
        if (noSqlLlmEntity == null)
            throw new ServerException().new InternalError("Unable to find the equivalent MongoDB instance.");
        WritingSession sqlLlmEntity = writingSessionRepository.findByMongoTopicId(noSqlLlmEntity.getId().toHexString());
        return TopicResponseEvalServiceUtil.TopicUtil.buildDTO(sqlLlmEntity, noSqlLlmEntity);
    }

    @Override
    @Transactional
    public void removeTopicGenerationByRefId(long refId) {
        WritingSession writingSession = writingSessionRepository.findByRefId(refId);
        if (StringUtils.isNotEmpty(writingSession.getMongoTopicId())) {
            TopicGeneration topicGeneration = topicGenerationRepository
                    .findById(new ObjectId(writingSession.getMongoTopicId())).orElse(null);
            if (topicGeneration != null) topicGenerationRepository.delete(topicGeneration);
        }
        if (StringUtils.isNotEmpty(writingSession.getMongoTopicResponseId())) {
            ResponseMaster responseMaster = responseMasterRepository
                    .findById(new ObjectId(writingSession.getMongoTopicResponseId())).orElse(null);
            if (responseMaster != null) responseMasterRepository.delete(responseMaster);
        }
        writingSessionRepository.delete(writingSession);
    }

    @Override
    @Transactional
    public void removeAllTopicGenerations() {
        writingSessionRepository.findAll().forEach(ws -> removeTopicGenerationByRefId(ws.getRefId()));
    }

    @Override
    public List<TopicDTO> findAllTopics() {
        List<TopicGeneration> topicGenerations = topicGenerationRepository.findAll();
        List<TopicDTO> topicDTOS = new LinkedList<>();
        topicGenerations.forEach(topicGeneration -> {
            List<TopicDTO> dtos = CollectionUtils.isNotEmpty(topicGeneration.getTopics())
                    ? topicGeneration.getTopics().stream().map(TopicResponseEvalServiceUtil.TopicUtil::buildDTO).toList()
                    : Collections.emptyList();
            if (CollectionUtils.isNotEmpty(dtos)) {
                WritingSession writingSession = writingSessionRepository.findByMongoTopicId(topicGeneration.getId().toHexString());
                if (writingSession == null) {
                    log.warn("WritingSession not found for TopicGeneration: {}", topicGeneration.getId());
                    // Still add the DTOs but without WritingSessionRefId
                    dtos.forEach(dto -> {
                        dto.setRecommendations(topicGeneration.getRecommendations());
                    });
                } else {
                    dtos.forEach(dto -> {
                        dto.setRecommendations(topicGeneration.getRecommendations());
                        dto.setWritingSessionRefId(String.valueOf(writingSession.getRefId()));
                    });
                }
                topicDTOS.addAll(dtos);
            }
        });
        return topicDTOS;
    }

    @Override
    public TopicDTO findTopicByRefId(long refId) {
        TopicGeneration topicGeneration = topicGenerationRepository.findByTopicRefId(refId)
                .orElseThrow(() -> new ServerException().new InternalError("TopicGeneration not found for refId: " + refId));
        if (CollectionUtils.isEmpty(topicGeneration.getTopics())) {
            throw new ServerException().new InternalError("No topics found in TopicGeneration");
        }
        Topic topic = topicGeneration.getTopics().stream()
                .filter(t -> t.getRefId() == refId)
                .findAny()
                .orElseThrow(() -> new ServerException().new InternalError("Topic not found for refId: " + refId));
        WritingSession writingSession = writingSessionRepository.findByMongoTopicId(topicGeneration.getId().toHexString());
        if (writingSession == null) {
            throw new ServerException().new InternalError("WritingSession object is not found");
        }
        TopicDTO topicDTO = TopicResponseEvalServiceUtil.TopicUtil.buildDTO(topic);
        topicDTO.setRecommendations(topicGeneration.getRecommendations());
        topicDTO.setWritingSessionRefId(String.valueOf(writingSession.getRefId()));
        return topicDTO;
    }

    @Override
    public void removeTopicByRefId(long refId) {
    }
}
