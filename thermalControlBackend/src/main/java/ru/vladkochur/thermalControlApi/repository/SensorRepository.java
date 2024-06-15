package ru.vladkochur.thermalControlApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findBySerial(Integer serial);
    void deleteSensorBySerial(Integer serial);

}
