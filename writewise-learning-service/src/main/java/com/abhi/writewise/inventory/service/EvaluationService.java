package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.entities.nosql.mongodb.evaluation.EvaluationErrorList;

import java.util.List;

public interface EvaluationService {
    List<EvaluationErrorList> evaluateLowLevel(String text);
}
