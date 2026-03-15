package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.rec.RecommendationDTO;
import com.abhi.leximentor.fitmate.dto.rec.RecommendationRequestDTO;

public interface RecommendationService {

    /**
     * Generates a ranked list of exercise recommendations for the given user
     * and target body part.
     *
     * <p>Phase 1 implementation uses a rule-based engine (progressive overload +
     * neglect/variety scoring). Phase 2 will delegate to the Python ML microservice
     * when sufficient training data is available, falling back to the rule engine.
     *
     * @param request user, body part, optional training type, and result limit
     * @return ranked recommendations with load targets and progression notes
     */
    RecommendationDTO recommend(RecommendationRequestDTO request);
}
