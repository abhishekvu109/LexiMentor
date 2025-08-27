package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.constants.Status;
import com.abhi.writewise.inventory.dto.response.ResponseDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseVersion;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.ResponseRepository;
import com.abhi.writewise.inventory.repository.nosql.impl.RepositoryUtil;
import com.abhi.writewise.inventory.service.ResponseAndEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResponseAndEvaluationServiceImpl implements ResponseAndEvaluationService {
    private static final Integer DRAFT = 0;
    private static final Integer SUBMITTED = 1;
    private static final Integer FIRST_VERSION = 1;
    private final ResponseMasterRepository responseMasterRepository;
    private final ResponseRepository responseRepository;
    private final RepositoryUtil repositoryUtil;

    @Override
    @Transactional
    public ResponseMasterDTO doSubmitResponse(long sqlRefId, long topicRefId, String response) {
        return doSaveResponse(sqlRefId, topicRefId, response, SUBMITTED);
    }

    @Override
    @Transactional
    public ResponseMasterDTO doSaveAsDraft(long sqlRefId, long topicRefId, String response) {
        return doSaveResponse(sqlRefId, topicRefId, response, DRAFT);
    }

    private ResponseMasterDTO doSaveResponse(long sqlRefId, long topicRefId, String response, int requestType) {
        ResponseMaster responseMaster = repositoryUtil.findResponseMasterByTopicRefId(topicRefId);
        Response updatedResponse = repositoryUtil.findResponseByTopicRefId(responseMaster, topicRefId);
        ResponseVersion responseVersion;
        boolean isNewResponseVersion = false;
        // If the response is in draft/ not-submitted then DO NOT create a new Response Version
        //If it is the first response version
        if (CollectionUtils.isEmpty(updatedResponse.getResponseVersions())) {
            isNewResponseVersion = true;
            responseVersion = TopicResponseEvalServiceUtil.ResponseUtil.BuildEntity.buildResponseVersion();
            responseVersion.setLatest(true);
            responseVersion.setVersionNumber(FIRST_VERSION);
        } else if (isExistingResponseVersionInProgress(updatedResponse)) {
            List<ResponseVersion> responseVersions = updatedResponse.getResponseVersions().stream().filter(rv -> rv.getResponseStatus() == Status.TopicResponse.IN_PROGRESS).sorted(Comparator.comparing(ResponseVersion::getCreateDate).reversed()).toList();
            if (CollectionUtils.isNotEmpty(responseVersions)) {
                responseVersion = responseVersions.get(0);
            } else {
                throw new ServerException().new InternalError("In-progress Response Version is not found.");
            }

        } else {

            isNewResponseVersion = true;
            ResponseVersion prevLatest = updatedResponse.getResponseVersions().stream().filter(ResponseVersion::isLatest).findAny().orElse(null);
            if (prevLatest == null) {
                log.error("Unable to find the previous version that is latest");
                throw new ServerException().new InternalError("Previous latest Response version not found");
            }
            prevLatest.setLatest(false);
            responseVersion = TopicResponseEvalServiceUtil.ResponseUtil.BuildEntity.buildResponseVersion();
            responseVersion.setLatest(true);
            ResponseVersion maxResponseVersion = updatedResponse.getResponseVersions().stream().filter(rv -> rv.getResponseStatus() == Status.TopicResponse.SUBMITTED).max(Comparator.comparing(ResponseVersion::getResponseStatus)).orElse(null);
            if (maxResponseVersion == null) {
                log.error("Unable to find a response version that is in-progress/not-started and has maximum version number.");
                throw new ServerException().new InternalError("Unable to find the valid Response Version object.");
            }
            responseVersion.setVersionNumber(maxResponseVersion.getVersionNumber() + 1);
        }
        responseVersion.setLastUpdDate(LocalDateTime.now());
        responseVersion.setResponse(response);
        responseVersion.setResponseStatus((requestType == SUBMITTED) ? Status.TopicResponse.SUBMITTED : Status.TopicResponse.IN_PROGRESS);
        if (isNewResponseVersion) updatedResponse.getResponseVersions().add(responseVersion);
        responseMaster = responseMasterRepository.save(responseMaster);
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponseMaster(responseMaster);
    }


    private boolean isExistingResponseVersionInProgress(Response response) {
        List<ResponseVersion> responseVersions = response.getResponseVersions().stream().filter(rv -> rv.getResponseStatus() != Status.TopicResponse.SUBMITTED).toList();
        return (CollectionUtils.isNotEmpty(responseVersions));
    }

    @Override
    public ResponseDTO getResponseByTopicRefId(long topicRefId) {
        Response response = responseRepository.findByTopicRefId(topicRefId);
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponse(response);
    }

    @Override
    public List<ResponseDTO> submittedResponses() {
        List<Response> responses = responseMasterRepository.findAll().stream().flatMap(rm -> rm.getTopicResponseList().stream()).filter(res -> CollectionUtils.isNotEmpty(res.getResponseVersions())).toList();
        responses.forEach(response -> {
            List<ResponseVersion> responseVersions = response.getResponseVersions().stream().filter(rv -> rv.getEvaluation() != null && (rv.getResponseStatus() == Status.TopicResponse.SUBMITTED) && rv.getEvaluation().getEvaluationStatus() == Status.EvaluationStatus.NOT_STARTED).toList();
            response.setResponseVersions(responseVersions);
        });
        return responses.stream().map(TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO::buildResponse).toList();
    }

    @Override
    public List<ResponseDTO> draftedResponse() {
        return null;
    }

    @Override
    public List<ResponseDTO> getEvaluatedTopics() {
        List<Response> responses = responseMasterRepository.findAll()
                .stream()
                .flatMap(rm -> rm.getTopicResponseList().stream())
                .filter(response -> {
                    if (CollectionUtils.isNotEmpty(response.getResponseVersions())) {
                        return response.getResponseVersions().stream()
                                .filter(rv -> rv.getEvaluation() != null && rv.getEvaluation().getEvaluationStatus() == Status.EvaluationStatus.COMPLETED)
                                .findAny()
                                .orElse(null) != null;
                    } else {
                        return false;
                    }
                }).toList();
        responses.forEach(response -> {
            response.setResponseVersions(response.getResponseVersions().stream()
                    .filter(rv -> rv.getEvaluation() != null && rv.getEvaluation().getEvaluationStatus() == Status.EvaluationStatus.COMPLETED)
                    .toList());
        });
        return responses.stream().map(TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO::buildResponse).toList();
    }

    @Override
    public ResponseDTO getResponseByResponseRefIdAndVersionRefId(long responseRefId, long versionRefId) {
        Response response = responseRepository.findByRefId(responseRefId);
        if (CollectionUtils.isEmpty(response.getResponseVersions())) {
            throw new ServerException().new InternalError("ResponseVersion object is not found.");
        }
        ResponseVersion responseVersion = response.getResponseVersions().stream()
                .filter(rv -> rv.getRefId() == versionRefId)
                .findAny()
                .orElseThrow(() -> new ServerException().new InternalError("No response version found."));
        List<ResponseVersion> responseVersions = new ArrayList<>();
        responseVersions.add(responseVersion);
        response.setResponseVersions(responseVersions);
        return TopicResponseEvalServiceUtil.ResponseUtil.BuildDTO.buildResponse(response);
    }
}
