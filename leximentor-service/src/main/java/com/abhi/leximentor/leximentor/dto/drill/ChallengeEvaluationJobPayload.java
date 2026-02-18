package com.abhi.leximentor.leximentor.dto.drill;

public class ChallengeEvaluationJobPayload {
    private long challengeRefId;
    private String evaluator;

    public ChallengeEvaluationJobPayload() {
    }

    public ChallengeEvaluationJobPayload(long challengeRefId, String evaluator) {
        this.challengeRefId = challengeRefId;
        this.evaluator = evaluator;
    }

    public long getChallengeRefId() {
        return challengeRefId;
    }

    public void setChallengeRefId(long challengeRefId) {
        this.challengeRefId = challengeRefId;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(String evaluator) {
        this.evaluator = evaluator;
    }
}
