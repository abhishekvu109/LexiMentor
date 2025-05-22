package com.abhi.leximentor.ai.service;

public interface LLMPromptService {
    String execute(String prompt);
    String execute(String prompt,String format);
    String execute(String prompt,String format,String model);
}
