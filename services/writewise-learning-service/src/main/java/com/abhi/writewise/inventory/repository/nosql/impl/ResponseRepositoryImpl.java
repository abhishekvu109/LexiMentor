package com.abhi.writewise.inventory.repository.nosql.impl;

import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.ResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseRepositoryImpl implements ResponseRepository {

    private final ResponseMasterRepository responseMasterRepository;

    @Override
    public Response findByRefId(long refId) {
        return responseMasterRepository.findByResponseRefId(refId)
                .map(rm -> rm.getTopicResponseList().stream()
                        .filter(res -> res.getRefId() == refId)
                        .findFirst()
                        .orElseThrow(() -> new ServerException().new InternalError("Response not found for refId: " + refId)))
                .orElseThrow(() -> new ServerException().new InternalError("ResponseMaster not found for refId: " + refId));
    }

    @Override
    public Response findByUuid(String uuid) {
        return responseMasterRepository.findByResponseUuid(uuid)
                .map(rm -> rm.getTopicResponseList().stream()
                        .filter(res -> uuid.equals(res.getUuid()))
                        .findFirst()
                        .orElseThrow(() -> new ServerException().new InternalError("Response not found for uuid: " + uuid)))
                .orElseThrow(() -> new ServerException().new InternalError("ResponseMaster not found for uuid: " + uuid));
    }

    @Override
    public Response findByResponseMasterAndRefId(String id, long refId) {
        ResponseMaster responseMaster = responseMasterRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ServerException().new EntityObjectNotFound("Unable to find mongo response object"));
        return responseMaster.getTopicResponseList().stream()
                .filter(res -> res.getRefId() == refId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Response findByTopicRefId(long topicRefId) {
        ResponseMaster responseMaster = responseMasterRepository.findByTopicRefId(topicRefId)
                .orElseThrow(() -> new ServerException().new InternalError("Response Object is not found for topicRefId: " + topicRefId));
        return responseMaster.getTopicResponseList().stream()
                .filter(res -> res.getTopic() != null && res.getTopic().getRefId() == topicRefId)
                .findFirst()
                .orElseThrow(() -> new ServerException().new InternalError("Response not found for topicRefId: " + topicRefId));
    }
}
