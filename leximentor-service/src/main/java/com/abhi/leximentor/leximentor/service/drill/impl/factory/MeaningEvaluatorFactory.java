package com.abhi.leximentor.leximentor.service.drill.impl.factory;

import com.abhi.leximentor.leximentor.dto.other.LlamaModelDTO;

public interface MeaningEvaluatorFactory {
    public LlamaModelDTO response(String prompt,int retryCount);
}
