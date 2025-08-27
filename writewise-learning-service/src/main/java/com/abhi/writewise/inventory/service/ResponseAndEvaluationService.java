package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.response.ResponseDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;

import java.util.List;

public interface ResponseAndEvaluationService {
    ResponseMasterDTO doSubmitResponse(long sqlRefId, long topicRefId, String response);

    ResponseMasterDTO doSaveAsDraft(long sqlRefId, long topicRefId, String response);

    ResponseDTO getResponseByTopicRefId(long topicRefId);

    ResponseDTO getResponseByResponseRefIdAndVersionRefId(long responseRefId, long versionRefId);

    List<ResponseDTO> submittedResponses();

    List<ResponseDTO> draftedResponse();

    List<ResponseDTO> getEvaluatedTopics();

}
