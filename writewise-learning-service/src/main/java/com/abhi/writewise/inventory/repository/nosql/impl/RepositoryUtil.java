package com.abhi.writewise.inventory.repository.nosql.impl;

import com.abhi.writewise.inventory.entities.nosql.mongodb.response.Response;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseMaster;
import com.abhi.writewise.inventory.entities.nosql.mongodb.response.ResponseVersion;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.Topic;
import com.abhi.writewise.inventory.entities.nosql.mongodb.topic.TopicGeneration;
import com.abhi.writewise.inventory.exceptions.entities.ServerException;
import com.abhi.writewise.inventory.repository.nosql.ResponseMasterRepository;
import com.abhi.writewise.inventory.repository.nosql.TopicGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RepositoryUtil {
    private final ResponseMasterRepository responseMasterRepository;
    private final TopicGenerationRepository topicGenerationRepository;


    public ResponseMaster findResponseMasterByTopicRefId(long topicRefId) {
        return responseMasterRepository.findByTopicRefId(topicRefId)
                .orElseThrow(() -> new ServerException().new InternalError("ResponseMaster not found for topicRefId: " + topicRefId));
    }

    public Response findResponseByTopicRefId(long topicRefId) {
        ResponseMaster responseMaster = findResponseMasterByTopicRefId(topicRefId);
        return findResponseByTopicRefId(responseMaster, topicRefId);
    }

    public Response findResponseByTopicRefId(ResponseMaster responseMaster, long topicRefId) {
        return responseMaster.getTopicResponseList().stream()
                .filter(res -> res.getTopic() != null && res.getTopic().getRefId() == topicRefId)
                .findAny()
                .orElseThrow(() -> new ServerException().new InternalError("Response not found for topicRefId: " + topicRefId));
    }

    public ResponseVersion findResponseVersionByTopicRefIdAndVersionRefId(ResponseMaster responseMaster, long topicRefId, long versionRefId) {
        Response response = findResponseByTopicRefId(responseMaster, topicRefId);
        return response.getResponseVersions().stream()
                .filter(rv -> rv.getRefId() == versionRefId)
                .findAny()
                .orElseThrow(() -> new ServerException().new InternalError("ResponseVersion not found for versionRefId: " + versionRefId));
    }

    public ResponseVersion findResponseVersionByTopicRefIdAndVersionRefId(long topicRefId, long versionRefId) {
        ResponseMaster responseMaster = findResponseMasterByTopicRefId(topicRefId);
        return findResponseVersionByTopicRefIdAndVersionRefId(responseMaster, topicRefId, versionRefId);
    }

    public Topic findTopicByTopicRefId(long topicRefId) {
        TopicGeneration topicGeneration = findTopicGenerationByTopicRefId(topicRefId);
        return topicGeneration.getTopics().stream()
                .filter(t -> t.getRefId() == topicRefId)
                .findAny()
                .orElseThrow(() -> new ServerException().new InternalError("Topic not found for topicRefId: " + topicRefId));
    }

    public TopicGeneration findTopicGenerationByTopicRefId(long topicRefId) {
        return topicGenerationRepository.findByTopicRefId(topicRefId)
                .orElseThrow(() -> new ServerException().new InternalError("TopicGeneration not found for topicRefId: " + topicRefId));
    }
}
