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
        return responseMasterRepository.findAll().stream().filter(rm -> {
            if (CollectionUtils.isNotEmpty(rm.getTopicResponseList())) {
                Response response = rm.getTopicResponseList().stream().filter(res -> res.getTopic() != null && res.getTopic().getRefId() == topicRefId).findAny().orElseThrow(() -> new ServerException().new InternalError("Response object is null"));
                return response != null;

            } else {
                return false;
            }
        }).findFirst().orElseThrow(() -> new ServerException().new InternalError("Response Master is null"));
    }

    public Response findResponseByTopicRefId(long topicRefId) {
        ResponseMaster responseMaster = findResponseMasterByTopicRefId(topicRefId);
        return responseMaster.getTopicResponseList().stream().filter(res -> (res.getTopic() != null && res.getTopic().getRefId() == topicRefId)).findAny().orElseThrow(() -> new ServerException().new InternalError("Response entity not found."));
    }

    public Response findResponseByTopicRefId(ResponseMaster responseMaster, long topicRefId) {
        return responseMaster.getTopicResponseList().stream().filter(res -> (res.getTopic() != null && res.getTopic().getRefId() == topicRefId)).findAny().orElseThrow(() -> new ServerException().new InternalError("Response entity not found."));
    }

    public ResponseVersion findResponseVersionByTopicRefIdAndVersionRefId(ResponseMaster responseMaster, long topicRefId, long versionRefId) {
        return responseMaster.getTopicResponseList().stream().filter(res -> res.getTopic() != null && res.getTopic().getRefId() == topicRefId).findFirst().orElseThrow(() -> new ServerException().new InternalError("Response object is null")).getResponseVersions().stream().filter(rv -> rv.getRefId() == versionRefId).findAny().orElseThrow(() -> new ServerException().new InternalError("ResponseVersion object is null"));
    }

    public ResponseVersion findResponseVersionByTopicRefIdAndVersionRefId(long topicRefId, long versionRefId) {
        ResponseMaster responseMaster = findResponseMasterByTopicRefId(topicRefId);
        return responseMaster.getTopicResponseList().stream().filter(res -> res.getTopic() != null && res.getTopic().getRefId() == topicRefId).findFirst().orElseThrow(() -> new ServerException().new InternalError("Response object is null")).getResponseVersions().stream().filter(rv -> rv.getRefId() == versionRefId).findAny().orElseThrow(() -> new ServerException().new InternalError("ResponseVersion object is null"));
    }

    public Topic findTopicByTopicRefId(long topicRefId) {
        return topicGenerationRepository.findAll().stream().flatMap(tg -> tg.getTopics().stream()).filter(t -> t.getRefId() == topicRefId).findAny().orElseThrow(() -> new ServerException().new InternalError("Topic Object is not found."));
    }

    public TopicGeneration findTopicGenerationByTopicRefId(long topicRefId) {
        return topicGenerationRepository.findAll().stream().filter(tg -> {
            Topic topic = tg.getTopics().stream().filter(t -> t.getRefId() == topicRefId).findAny().orElse(null);
            return topic != null;
        }).findAny().orElseThrow(() -> new ServerException().new InternalError("TopicGeneration object is not found"));
    }
}
