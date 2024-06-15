package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.repository.SensorPeriodRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorPeriodServiceImpl implements SensorPeriodService {
    private final SensorPeriodRepository sensorPeriodRepository;

    @Override
    public SensorPeriod setOptimalTemperaturePeriod(SensorPeriod sensorPeriod, boolean isDefault) {
        Optional<SensorPeriod> obtainedSensorPeriod = sensorPeriodRepository
                .findByWeekdayAndIsDefault(sensorPeriod.getWeekday(), isDefault);
        if (obtainedSensorPeriod.isPresent()) {
            SensorPeriod periodToSave = obtainedSensorPeriod.get();
            periodToSave.setStartAt(sensorPeriod.getStartAt());
            periodToSave.setEndAt(sensorPeriod.getEndAt());
            return sensorPeriodRepository.save(periodToSave);
        } else {
            sensorPeriod.setIsDefault(isDefault);
            return sensorPeriodRepository.save(sensorPeriod);
        }
    }

    @Override
    public List<SensorPeriod> getAll() {
        return sensorPeriodRepository.findAllOrderByWeekday();
    }

    @Override
    public List<SensorPeriod> getActual() {
        return sensorPeriodRepository.findAllByIsDefaultFalseOrderByWeekday();
    }

    @Override
    public Optional<SensorPeriod> getPeriodByWeekday(Weekday weekday, boolean isDefault) {
        return sensorPeriodRepository.findByWeekdayAndIsDefault(weekday, isDefault);
    }

    @Override
    public void deleteAllActual() {
        sensorPeriodRepository.deleteAllByIsDefaultFalse();
    }
}
