package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface SensorPeriodService {

    List<SensorPeriod> getAll();

    public List<SensorPeriod> getActual();

    Optional<SensorPeriod> getPeriodByWeekday(Weekday weekday, boolean isDefault);

    @Transactional
    public void deleteAllActual();

    @Transactional
    SensorPeriod setOptimalTemperaturePeriod(SensorPeriod sensorPeriod, boolean isDefault);


}
