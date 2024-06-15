package ru.vladkochur.thermalControlApi.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorSetup {

    @NotNull(message = "You must provide sensor serial number")
    private Integer serial;

    @NotNull(message = "You must provide optimal temperature start time")
    private LocalTime startAt;

    @NotNull(message = "You must provide optimal temperature end time")
    private LocalTime endAt;

    @NotNull(message = "You must provide optimal temperature")
    private Double optimalTemperature;

    @NotNull(message = "You must provide minimal temperature")
    private Double minimalTemperature;

}
