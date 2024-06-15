package ru.vladkochur.thermalControlApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SensorNotFoundException extends ResponseStatusException {
    public SensorNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Sensor is not exist!");
    }
}
