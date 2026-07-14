package br.com.foodhub.domain.exception;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String resource) {
        super(resource + " is currently in use and cannot be removed.");
    }
}
