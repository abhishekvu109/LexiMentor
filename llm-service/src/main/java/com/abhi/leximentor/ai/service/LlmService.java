package com.abhi.leximentor.ai.service;

import com.abhi.leximentor.ai.dto.MeaningEvaluationDTO;

public interface LlmService {
    public MeaningEvaluationDTO evaluateWordMeaning(String prompt);

    public String prompt(String text);
    public String prompt(String text,String modelName);
    public String prompt(String text,String modelName,String format);

}
