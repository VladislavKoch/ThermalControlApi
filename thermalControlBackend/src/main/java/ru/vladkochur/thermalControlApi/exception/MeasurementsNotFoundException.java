package ru.vladkochur.thermalControlApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MeasurementsNotFoundException extends ResponseStatusException {
    public MeasurementsNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "Measurements are not exist!");
    }
}
