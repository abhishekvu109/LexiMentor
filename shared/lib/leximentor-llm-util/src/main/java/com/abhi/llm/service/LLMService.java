package com.abhi.llm.service;

import com.abhi.llm.model.PromptRequest;
import com.abhi.llm.model.PromptResponse;

public interface LLMService {
    PromptResponse execute(PromptRequest request);

    void setURL(String URL);
}
