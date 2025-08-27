package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;

import java.util.List;

public interface TopicService {
    TopicGenerationDTO addTopicGenerationsUsingLLM(TopicGenerationDTO request);

    List<TopicGenerationDTO> findAllTopicGenerations();

    TopicGenerationDTO findTopicGenerationByRefId(long refId);

    void removeTopicGenerationByRefId(long refId);

    void removeAllTopicGenerations();

    List<TopicDTO> findAllTopics();

    TopicDTO findTopicByRefId(long refId);

    void removeTopicByRefId(long refId);
}
