package com.abhi.flashcard.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String refId) {
        super(resource + " not found with refId: " + refId);
    }
}
