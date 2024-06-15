package ru.vladkochur.thermalControlApi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorSetupDTO {

    @NotNull(message = "You must provide sensor serial number")
    private Integer serial;

    @NotNull(message = "You must provide optimal temperature start time")
    private Integer startAt;

    @NotNull(message = "You must provide optimal temperature end time")
    private Integer endAt;

    @NotNull(message = "You must provide optimal temperature")
    private Double optimalTemperature;

    @NotNull(message = "You must provide minimal temperature")
    private Double minimalTemperature;

}
