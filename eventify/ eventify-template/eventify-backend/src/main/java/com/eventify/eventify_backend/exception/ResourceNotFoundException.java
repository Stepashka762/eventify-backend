package com.eventify.eventify_backend.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
                String.format("%s not found with id: %d", resourceName, id),
                "RESOURCE_NOT_FOUND",
                "ERROR"
        );
    }

    public ResourceNotFoundException(String resourceName, String email) {
        super(
                String.format("%s not found with email: %s", resourceName, email),
                "RESOURCE_NOT_FOUND",
                "ERROR"
        );
    }
}