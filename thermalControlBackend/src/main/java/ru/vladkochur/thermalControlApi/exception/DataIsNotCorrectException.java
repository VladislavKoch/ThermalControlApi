package ru.vladkochur.thermalControlApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DataIsNotCorrectException extends ResponseStatusException {
    public DataIsNotCorrectException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
