package ru.vladkochur.thermalControlApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(value = -100, message = "Temperature should be above than -100 degrees")
    @Max(value = 200, message = "Temperature should be below than 200 degrees")
    @NotNull(message = "You must provide temperature")
    private Double temperature;

    @Min(value = 0, message = "Humidity should be above than 0 percent")
    @Max(value = 100, message = "Humidity should be below than 100 percent")
    @NotNull(message = "You must provide humidity")
    private Double humidity;

    @NotNull(message = "You must provide sensor")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sensor", referencedColumnName = "serial")
    private Sensor sensor;

    private LocalDateTime time;
}
