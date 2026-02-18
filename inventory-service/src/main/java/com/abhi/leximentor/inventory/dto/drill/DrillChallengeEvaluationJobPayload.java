package com.abhi.leximentor.inventory.dto.drill;

public class DrillChallengeEvaluationJobPayload {
    private long challengeRefId;
    private String evaluator;

    public DrillChallengeEvaluationJobPayload() {
    }

    public DrillChallengeEvaluationJobPayload(long challengeRefId, String evaluator) {
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
