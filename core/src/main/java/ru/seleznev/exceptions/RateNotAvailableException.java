package ru.seleznev.exceptions;

public class RateNotAvailableException extends RuntimeException {
    public RateNotAvailableException(String message) {
        super(message);
    }
}
