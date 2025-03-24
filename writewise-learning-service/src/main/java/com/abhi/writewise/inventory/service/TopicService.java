package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.LlmTopicDTO;

public interface TopicService {
    public LlmTopicDTO generateTopicsFromLlm(LlmTopicDTO request);
}
