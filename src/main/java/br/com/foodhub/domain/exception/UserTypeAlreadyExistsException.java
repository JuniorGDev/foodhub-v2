package br.com.foodhub.domain.exception;

public class UserTypeAlreadyExistsException extends RuntimeException {
    public UserTypeAlreadyExistsException(String message) {
        super("User type already exists with name: " + message);
    }
}
