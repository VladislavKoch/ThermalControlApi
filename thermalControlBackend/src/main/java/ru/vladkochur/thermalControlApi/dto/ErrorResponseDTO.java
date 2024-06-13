package ru.vladkochur.thermalControlApi.dto;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String message;
    private long timestamp;

    public ErrorResponseDTO(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
