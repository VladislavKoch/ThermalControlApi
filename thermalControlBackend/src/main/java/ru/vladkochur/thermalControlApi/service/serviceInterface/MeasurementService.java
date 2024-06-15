package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.Measurement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MeasurementService {
    public List<Measurement> findAll();

    List<Measurement> findLastWeekMeasurements();

    List<Measurement> findAllMeasurementsBySensorSerial(Integer serial);

    Optional<Measurement> findAvgMeasurementBySensorSerial(Integer serial);

    List<Measurement> findLastWeekMeasurementsBySensorSerial(Integer serial);

    List<Measurement> findDailyMeasurementsBySensorSerial(Integer serial);

    Optional<Measurement> findAvgMeasurement();

    @Transactional
    void deleteMeasurementsByTimeBefore(LocalDateTime time);

    @Transactional
    Measurement save(Measurement measurement);

    void sendCriticalMeasurementToAllUsers(Measurement measurement);
}
