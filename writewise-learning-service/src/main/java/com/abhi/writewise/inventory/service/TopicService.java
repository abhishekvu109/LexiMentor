package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.LlmTopicDTO;

import java.util.List;

public interface TopicService {
    public LlmTopicDTO generateTopicsFromLlm(LlmTopicDTO request);

    public List<LlmTopicDTO> findAll();

    public LlmTopicDTO findByRefId(long refId);
}
