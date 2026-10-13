package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.topic.CreateTopicManuallyDTO;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;

import java.util.List;

public interface TopicService {
    TopicGenerationDTO addTopicGenerationsUsingLLM(TopicGenerationDTO request);

    TopicGenerationDTO addTopicManually(CreateTopicManuallyDTO request);

    List<TopicGenerationDTO> findAllTopicGenerations();

    TopicGenerationDTO findTopicGenerationByRefId(long refId);

    void removeTopicGenerationByRefId(long refId);

    void removeAllTopicGenerations();

    List<TopicDTO> findAllTopics();

    TopicDTO findTopicByRefId(long refId);

    void removeTopicByRefId(long refId);
}
