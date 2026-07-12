package br.com.foodhub.domain.exception;

public class InvalidBusinessHoursException extends RuntimeException {
    public InvalidBusinessHoursException() {
        super("Invalid business hours.");
    }
}
