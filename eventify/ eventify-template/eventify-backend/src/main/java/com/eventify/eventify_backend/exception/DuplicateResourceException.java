package com.eventify.eventify_backend.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resourceName, String value) {
        super(
                String.format("%s already exists with value: %s", resourceName, value),
                "DUPLICATE_RESOURCE",
                "WARNING"
        );
    }
}