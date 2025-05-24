package com.abhi.writewise.inventory.repository.nosql;

import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicGenerationRepository extends MongoRepository<TopicGeneration, ObjectId> {
    TopicGeneration findByRefId(long refId);
    TopicGeneration findByUuid(String uuid);
    List<TopicGeneration> findBySubject(String subject);
}
