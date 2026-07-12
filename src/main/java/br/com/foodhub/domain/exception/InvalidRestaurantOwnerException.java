package br.com.foodhub.domain.exception;

public class InvalidRestaurantOwnerException extends RuntimeException {
    public InvalidRestaurantOwnerException() {
        super("Only OWNER users can own a restaurant.");
    }
}
