package com.abhi.writewise.inventory.repository.nosql;

import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseMasterRepository extends MongoRepository<ResponseMaster, ObjectId> {
    ResponseMaster findByRefId(long refId);

    ResponseMaster findByUuid(String uuid);

}
