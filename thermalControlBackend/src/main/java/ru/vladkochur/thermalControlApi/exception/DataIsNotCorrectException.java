package ru.vladkochur.thermalControlApi.exception;

public class DataIsNotCorrectException extends RuntimeException{
    public DataIsNotCorrectException(String message) {
        super(message);
    }
}
