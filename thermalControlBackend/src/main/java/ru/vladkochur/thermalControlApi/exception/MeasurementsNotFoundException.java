package ru.vladkochur.thermalControlApi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MeasurementsNotFoundException extends RuntimeException {
    public MeasurementsNotFoundException(String message) {
        super(message);
    }
    
}
