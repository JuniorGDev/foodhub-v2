package br.com.foodhub.domain.exception;

public class InvalidMenuItemPriceException extends RuntimeException {
    public InvalidMenuItemPriceException() {
        super("Invalid menu item price");
    }
}
