package com.abhi.writewise.inventory.repository.nosql;

import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;

import java.util.List;

public interface TopicRepository {
    Topic findByTopicGenerationRefIdAndRefId(long topicGenerationRefId, long refId);

    Topic findByTopicGenerationRefIdAndUuid(long topicGenerationRefId, String uuid);

    Topic findByWritingSessionRefIdAndRefId(long writingSessionRefId, long refId);

    Topic findByRefId(long refId);

    List<String> getRecommendationsByTopicRefId(long topicRefId);

}
