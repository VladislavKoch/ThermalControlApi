package ru.vladkochur.thermalControlApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;

import java.util.List;
import java.util.Optional;

public interface SensorSettingsRepository extends JpaRepository<SensorSettings, Integer> {

    @Query("from SensorSettings order by sensor.serial nulls first")
    List<SensorSettings> findAllOrdered();

    void deleteSensorSettingsBySensorSerial(Integer serial);

    Optional<SensorSettings> findAllBySensorIsNull();

    Optional<SensorSettings> findBySensorSerial(Integer serial);
}
