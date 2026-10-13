package com.abhi.saarthi.cashflow.exceptions.entities;

public class ServerException {
    public static class EntityObjectNotFound extends RuntimeException {
        public EntityObjectNotFound(String message) {
            super(message);
        }
    }

    public static class DuplicateEntityObjectFound extends RuntimeException {
        public DuplicateEntityObjectFound(String message) {
            super(message);
        }
    }

    public static class ChallengeAlreadyEvaluated extends RuntimeException {
        public ChallengeAlreadyEvaluated(String message) {
            super(message);
        }
    }

    public static class InternalError extends RuntimeException {
        public InternalError(String message) {
            super(message);
        }
    }


}