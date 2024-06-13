package ru.vladkochur.thermalControlApi.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorDTO {

    @NotNull(message = "You must provide sensor serial number")
    private Integer serial;

    @Size(min = 3, max = 100, message = "Name must have from 3 up to 100 symbols")
    private String name;

}
