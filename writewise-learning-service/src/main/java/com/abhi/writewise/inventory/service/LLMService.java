package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.model.PromptRequest;
import com.abhi.writewise.inventory.model.PromptResponse;

public interface LLMService {
    PromptResponse execute(PromptRequest request);
}
