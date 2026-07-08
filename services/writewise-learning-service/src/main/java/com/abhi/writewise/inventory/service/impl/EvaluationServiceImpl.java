package com.abhi.writewise.inventory.service.impl;

import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationErrorList;
import com.abhi.writewise.inventory.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    @Override
    public List<EvaluationErrorList> evaluateLowLevel(String text) {
        return Collections.emptyList();
    }
}
