package com.abhi.writewise.inventory.repository.nosql;


import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;

public interface ResponseRepository {
    Response findByRefId(long refId);

    Response findByUuid(String uuid);

    Response findByResponseMasterAndRefId(String id, long refId);
}
