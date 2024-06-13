package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.repository.SensorPeriodRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SensorPeriodServiceImpl implements SensorPeriodService {
    private final SensorPeriodRepository sensorPeriodRepository;

    @Override
    @Transactional
    public SensorPeriod setOptimalTemperaturePeriod(LocalTime startAt, LocalTime endAt,
                                                    Weekday weekday, boolean isDefault) {
        Optional<SensorPeriod> obtainedSensorPeriod =
                sensorPeriodRepository.findByWeekdayAndIsDefault(weekday, isDefault);
        if (obtainedSensorPeriod.isPresent()) {
            SensorPeriod periodToSave = obtainedSensorPeriod.get();
            periodToSave.setStartAt(startAt);
            periodToSave.setEndAt(endAt);
            return sensorPeriodRepository.save(periodToSave);
        } else {
            SensorPeriod sensorPeriod = SensorPeriod.builder()
                    .startAt(startAt)
                    .endAt(endAt)
                    .isDefault(isDefault)
                    .weekday(weekday)
                    .build();
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
        return sensorPeriodRepository.findByWeekdayAndIsDefault( weekday, isDefault);
    }

    @Override
    @Transactional
    public void deleteAllActual() {
        sensorPeriodRepository.deleteAllByIsDefaultFalse();
    }
}
