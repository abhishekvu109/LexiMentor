package com.abhi.writewise.inventory.repository.nosql.impl;

import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.ResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseRepositoryImpl implements ResponseRepository {

    private final ResponseMasterRepository responseMasterRepository;

    @Override
    public Response findByRefId(long refId) {
        List<ResponseMaster> responseMasters = responseMasterRepository.findAll();
        return responseMasters.stream().flatMap(rm -> rm.getTopicResponseList().stream()).toList().stream().filter(res -> res.getRefId() == refId).findFirst().orElse(null);
    }

    @Override
    public Response findByUuid(String uuid) {
        List<ResponseMaster> responseMasters = responseMasterRepository.findAll();
        return responseMasters.stream().flatMap(rm -> rm.getTopicResponseList().stream()).toList().stream().filter(res -> StringUtils.equals(res.getUuid(), uuid)).findFirst().orElse(null);
    }

    @Override
    public Response findByResponseMasterAndRefId(String id, long refId) {
        ResponseMaster responseMaster = responseMasterRepository.findById(new ObjectId(id)).orElse(null);
        if (responseMaster == null) {
            throw new ServerException().new EntityObjectNotFound("Unable to find mongo response object");
        }
        return responseMaster.getTopicResponseList().stream().filter(res -> res.getRefId() == refId).findFirst().orElse(null);
    }
}
