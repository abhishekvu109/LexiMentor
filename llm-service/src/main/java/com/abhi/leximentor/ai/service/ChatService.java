package com.abhi.leximentor.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChatService {

    private final OllamaChatModel ollamaChatModel;
    private static final String CHAT_MODEL_NAME = "llama3";

    public String getPromptResult(String prompt) {
        ChatResponse response = ollamaChatModel.call(new Prompt(prompt, OllamaOptions.create().withModel(CHAT_MODEL_NAME)));
        return response.getResult().getOutput().getContent();
    }

}
