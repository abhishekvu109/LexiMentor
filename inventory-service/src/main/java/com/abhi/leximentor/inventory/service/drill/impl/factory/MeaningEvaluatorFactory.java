package com.abhi.leximentor.inventory.service.drill.impl.factory;

import com.abhi.leximentor.inventory.dto.other.LlamaModelDTO;

public interface MeaningEvaluatorFactory {
    public LlamaModelDTO response(String prompt,int retryCount);
}
