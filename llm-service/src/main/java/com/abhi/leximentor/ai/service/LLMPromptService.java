package com.abhi.leximentor.ai.service;

import com.abhi.leximentor.ai.dto.OllamaDTO;
import com.abhi.leximentor.ai.dto.OllamaResponseDTO;

public interface LLMPromptService {
    String execute(String prompt);
    OllamaResponseDTO execute(OllamaDTO request);
    String execute(String prompt,String format,String model);
}
