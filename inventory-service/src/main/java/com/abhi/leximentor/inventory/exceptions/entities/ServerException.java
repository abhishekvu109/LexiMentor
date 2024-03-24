package com.abhi.leximentor.inventory.exceptions.entities;

public class ServerException {
    public class EntityObjectNotFound extends RuntimeException {
        public EntityObjectNotFound(String message) {
            super(message);
        }
    }

    public class DuplicateEntityObjectFound extends RuntimeException {
        public DuplicateEntityObjectFound(String message) {
            super(message);
        }
    }

    public class ChallengeAlreadyEvaluated extends RuntimeException {
        public ChallengeAlreadyEvaluated(String message) {
            super(message);
        }
    }
}
