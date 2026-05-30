package ru.seleznev.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.seleznev.dto.errors.ErrorResponse;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.exceptions.InsufficientFundsException;
import ru.seleznev.exceptions.RateNotAvailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientFunds(InsufficientFundsException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(RateNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRateNotAvailable(RateNotAvailableException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
