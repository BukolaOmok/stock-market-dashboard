package org.bukola.stockmarket.exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
