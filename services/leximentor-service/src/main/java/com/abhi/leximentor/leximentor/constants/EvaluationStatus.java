package com.abhi.leximentor.leximentor.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EvaluationStatus {
    NOT_INITIATED(0),
    IN_PROGRESS(1),
    COMPLETED(2),
    EVALUATED(3);

    private final int value;

    public static EvaluationStatus of(int value) {
        return switch (value) {
            case 0 -> NOT_INITIATED;
            case 1 -> IN_PROGRESS;
            case 2 -> COMPLETED;
            default -> EVALUATED;
        };
    }

    public static int of(EvaluationStatus type) {
        return switch (type) {
            case NOT_INITIATED -> NOT_INITIATED.value;
            case IN_PROGRESS -> IN_PROGRESS.value;
            case COMPLETED -> COMPLETED.value;
            default -> EVALUATED.value;
        };
    }

    public static int of(String type) {
        return switch (type.toUpperCase()) {
            case "NOT_INITIATED" -> 0;
            case "IN_PROGRESS" -> 1;
            case "COMPLETED" -> 2;
            default -> 3;
        };
    }
}
