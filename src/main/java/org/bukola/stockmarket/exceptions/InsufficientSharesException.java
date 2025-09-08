package org.bukola.stockmarket.exceptions;

public class InsufficientSharesException extends RuntimeException {
    public InsufficientSharesException(String message) {
        super(message);
    }
}
