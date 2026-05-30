package com.abhi.writewise.inventory.repository.nosql.impl;

import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.TopicGenerationRepository;
import com.abhi.writewise.inventory.repository.nosql.TopicRepository;
import com.abhi.writewise.inventory.repository.sql.mysql.WritingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicRepositoryImpl implements TopicRepository {
    private final WritingSessionRepository writingSessionRepository;
    private final TopicGenerationRepository topicGenerationRepository;

    @Override
    public Topic findByTopicGenerationRefIdAndRefId(long topicGenerationRefId, long refId) {
        TopicGeneration topicGeneration = topicGenerationRepository.findByRefId(topicGenerationRefId);
        if (topicGeneration == null) {
            log.error("Unable to find the topic generation mongo reference object");
            throw new ServerException().new EntityObjectNotFound("Unable to find the mongo object");
        }
        if (CollectionUtils.isEmpty(topicGeneration.getTopics())) {
            log.error("No topics found with the mongo object");
            throw new ServerException().new InternalError("Unable to find the associated mongo object");
        }
        return topicGeneration.getTopics().stream().filter(topic -> topic.getRefId() == refId).findFirst().orElse(null);
    }

    @Override
    public Topic findByTopicGenerationRefIdAndUuid(long topicGenerationRefId, String uuid) {
        TopicGeneration topicGeneration = topicGenerationRepository.findByRefId(topicGenerationRefId);
        if (topicGeneration == null) {
            log.error("Unable to find the topic generation mongo reference object");
            throw new ServerException().new EntityObjectNotFound("Unable to find the mongo object");
        }
        if (CollectionUtils.isEmpty(topicGeneration.getTopics())) {
            log.error("No topics found with the mongo object");
            throw new ServerException().new InternalError("Unable to find the associated mongo object");
        }
        return topicGeneration.getTopics().stream().filter(topic -> StringUtils.equals(topic.getUuid(), uuid)).findFirst().orElse(null);
    }

    @Override
    public Topic findByWritingSessionRefIdAndRefId(long writingSessionRefId, long refId) {
        WritingSession writingSession = writingSessionRepository.findByRefId(writingSessionRefId);
        if (writingSession == null) {
            log.error("Unable to find the writing session with refId: {}", writingSessionRefId);
            throw new ServerException().new EntityObjectNotFound("WritingSession not found");
        }
        TopicGeneration topicGeneration = topicGenerationRepository.findById(new ObjectId(writingSession.getMongoTopicId())).orElse(null);
        if (topicGeneration == null) {
            log.error("Unable to find the topic generation mongo reference object");
            throw new ServerException().new EntityObjectNotFound("Unable to find the mongo object");
        }
        if (CollectionUtils.isEmpty(topicGeneration.getTopics())) {
            log.error("No topics found with the mongo object");
            throw new ServerException().new InternalError("Unable to find the associated mongo object");
        }
        return topicGeneration.getTopics().stream().filter(topic -> topic.getRefId() == refId).findFirst().orElse(null);
    }

    @Override
    public Topic findByRefId(long refId) {
        TopicGeneration topicGeneration = topicGenerationRepository.findByTopicRefId(refId)
                .orElse(null);
        if (topicGeneration == null) {
            return null;
        }
        return topicGeneration.getTopics().stream()
                .filter(topic -> topic.getRefId() == refId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> getRecommendationsByTopicRefId(long topicRefId) {
        TopicGeneration topicGeneration = topicGenerationRepository.findByTopicRefId(topicRefId)
                .orElseThrow(() -> new ServerException().new InternalError("TopicGeneration with the topicRefId is not found: " + topicRefId));
        if (CollectionUtils.isNotEmpty(topicGeneration.getTopics())) {
            Topic topic = topicGeneration.getTopics().stream()
                    .filter(t -> t.getRefId() == topicRefId)
                    .findAny()
                    .orElse(null);
            if (topic != null) {
                return topicGeneration.getRecommendations();
            }
        }
        log.error("Topic not found in TopicGeneration for refId: {}", topicRefId);
        throw new ServerException().new InternalError("Topic not found for topicRefId: " + topicRefId);
    }
}
