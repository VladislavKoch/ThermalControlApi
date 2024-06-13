package ru.vladkochur.thermalControlApi.service.serviceInterface;

import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SensorPeriodService {

    public SensorPeriod setOptimalTemperaturePeriod(LocalTime startAt, LocalTime endAt, Weekday weekday, boolean isDefault);

    List<SensorPeriod> getAll();

    public List<SensorPeriod> getActual();

    Optional<SensorPeriod> getPeriodByWeekday(Weekday weekday, boolean isDefault);

    public void deleteAllActual();

}
