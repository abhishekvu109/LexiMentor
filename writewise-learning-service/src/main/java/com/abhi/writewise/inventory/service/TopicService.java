package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;

import java.util.List;

public interface TopicService {
    public TopicGenerationDTO generateTopicsFromLlm(TopicGenerationDTO request);

    public List<TopicGenerationDTO> findAll();

    public TopicGenerationDTO findByRefId(long refId);
}
