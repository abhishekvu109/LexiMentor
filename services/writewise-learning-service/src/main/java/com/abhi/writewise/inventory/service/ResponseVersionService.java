package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;

public interface ResponseVersionService extends InitModelService {
    ResponseVersionDTO getResponseVersion(long topicRefId, long versionRefId);

    String getPrompt(long topicRefId, long versionRefId, boolean isHighLevel);

    String doEvaluationForTopicAndVersion(long topicRefId, long versionRefId, boolean isHighLevel);

    ResponseVersionDTO doSubmitResult(long topicRefId, long versionRefId, boolean isHighLevel);

    boolean doValidateEvaluationResult(long topicRefId, long versionRefId, boolean isHighLevel);

    void doDeleteEvaluationAll(long topicRefId);
    void doDeleteEvaluationByVersion(long topicRefId,long versionRefId);
    void deleteResponseVersion(long topicRefId, long versionRefId);

}
