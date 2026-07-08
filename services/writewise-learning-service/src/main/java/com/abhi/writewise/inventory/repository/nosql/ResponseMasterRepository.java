package com.abhi.writewise.inventory.repository.nosql;

import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseMasterRepository extends MongoRepository<ResponseMaster, ObjectId> {
    ResponseMaster findByRefId(long refId);

    ResponseMaster findByUuid(String uuid);

    /**
     * Find ResponseMaster containing a topic with the given refId.
     * Uses MongoDB query at database level for efficiency.
     */
    @Query("{ 'topicResponseList.topic.refId': ?0 }")
    Optional<ResponseMaster> findByTopicRefId(long topicRefId);

    /**
     * Find ResponseMaster containing a response with the given refId.
     * Uses MongoDB query at database level for efficiency.
     */
    @Query("{ 'topicResponseList.refId': ?0 }")
    Optional<ResponseMaster> findByResponseRefId(long responseRefId);

    /**
     * Find ResponseMaster containing a response with the given UUID.
     * Uses MongoDB query at database level for efficiency.
     */
    @Query("{ 'topicResponseList.uuid': ?0 }")
    Optional<ResponseMaster> findByResponseUuid(String responseUuid);
}
