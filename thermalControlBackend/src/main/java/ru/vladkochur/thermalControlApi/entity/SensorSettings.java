package ru.vladkochur.thermalControlApi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sensor_settings")
public class SensorSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "sensor", referencedColumnName = "serial")
    private Sensor sensor;

    @Column(name = "optimal_temperature")
    private Double optimalTemperature;

    @Column(name = "minimal_temperature")
    private Double minimalTemperature;

}
