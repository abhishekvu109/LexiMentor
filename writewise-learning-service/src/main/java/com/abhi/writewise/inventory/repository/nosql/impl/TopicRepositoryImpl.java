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
        if (CollectionUtils.isNotEmpty(topicGeneration.getTopics())) {
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
        if (CollectionUtils.isNotEmpty(topicGeneration.getTopics())) {
            log.error("No topics found with the mongo object");
            throw new ServerException().new InternalError("Unable to find the associated mongo object");
        }
        return topicGeneration.getTopics().stream().filter(topic -> StringUtils.equals(topic.getUuid(), uuid)).findFirst().orElse(null);
    }

    @Override
    public Topic findByWritingSessionRefIdAndRefId(long writingSessionRefId, long refId) {
        WritingSession writingSession = writingSessionRepository.findByRefId(writingSessionRefId);
        TopicGeneration topicGeneration = topicGenerationRepository.findById(new ObjectId(writingSession.getMongoTopicId())).orElse(null);
        if (topicGeneration == null) {
            log.error("Unable to find the topic generation mongo reference object");
            throw new ServerException().new EntityObjectNotFound("Unable to find the mongo object");
        }
        if (CollectionUtils.isNotEmpty(topicGeneration.getTopics())) {
            log.error("No topics found with the mongo object");
            throw new ServerException().new InternalError("Unable to find the associated mongo object");
        }
        return topicGeneration.getTopics().stream().filter(topic -> topic.getRefId() == refId).findFirst().orElse(null);
    }

    @Override
    public Topic findByRefId(long refId) {
        List<TopicGeneration> topicGenerationList = topicGenerationRepository.findAll();
        return topicGenerationList.stream().filter(topicGeneration -> CollectionUtils.isNotEmpty(topicGeneration.getTopics())).toList().stream().flatMap(tg -> tg.getTopics().stream()).toList().stream().filter(topic -> topic.getRefId() == refId).findFirst().orElse(null);
    }
}
