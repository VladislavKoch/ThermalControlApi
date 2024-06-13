package ru.vladkochur.thermalControlApi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vladkochur.thermalControlApi.constants.Weekday;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sensor_period")
public class SensorPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Enumerated(EnumType.ORDINAL)
    private Weekday weekday;

    @Column(name = "start_at")
    private LocalTime startAt;

    @Column(name = "end_at")
    private LocalTime endAt;
}
