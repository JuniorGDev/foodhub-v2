package br.com.foodhub.domain.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resource, String name) {
        super(resource + " not found with name: " + name);
    }
}
