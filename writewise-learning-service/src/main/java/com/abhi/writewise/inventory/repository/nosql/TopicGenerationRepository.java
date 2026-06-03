package com.abhi.writewise.inventory.repository.nosql;

import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicGenerationRepository extends MongoRepository<TopicGeneration, ObjectId> {
    TopicGeneration findByRefId(long refId);

    TopicGeneration findByUuid(String uuid);

    List<TopicGeneration> findBySubject(String subject);

    /**
     * Find TopicGeneration containing a topic with the given refId.
     * Uses MongoDB query at database level for efficiency.
     */
    @Query("{ 'topics.refId': ?0 }")
    Optional<TopicGeneration> findByTopicRefId(long topicRefId);
}
