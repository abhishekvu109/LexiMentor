package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;

import java.util.List;

public interface TopicService {
    public TopicGenerationDTO addTopicGenerationsUsingLLM(TopicGenerationDTO request);

    public List<TopicGenerationDTO> findAllTopicGenerations();

    public TopicGenerationDTO findTopicGenerationByRefId(long refId);

    public void removeTopicGenerationByRefId(long refId);

    public void removeAllTopicGenerations();

    public List<TopicDTO> findAllTopics();
    public TopicDTO findTopicByRefId(long refId);
    public void removeTopicByRefId(long refId);
}
