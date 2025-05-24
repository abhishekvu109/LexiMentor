package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;

public interface ResponseAndEvaluationService {
    public ResponseMasterDTO submit(long sqlRefId, long topicRefId, String response);

    public ResponseMasterDTO saveAsDraft(long sqlRefId, long topicRefId,String response);

    public ResponseMasterDTO evaluate(long sqlRefId,long versionRefId,long topicRefId);
}
