package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;
import ru.vladkochur.thermalControlApi.entity.SensorSetup;
import ru.vladkochur.thermalControlApi.exception.DataIsNotCorrectException;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSettingsService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSetupService;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class SensorSetupServiceImpl implements SensorSetupService {
    private final SensorPeriodService sensorPeriodService;
    private final SensorService sensorService;
    private final SensorSettingsService sensorSettingsService;

    public SensorSetup getSetupBySerial(Integer serial) {
        Sensor obtainedSensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);

        SensorSettings settings = sensorSettingsService.getSettingsBySerial(serial).orElseGet(
                () -> sensorSettingsService.getDefaultSettings().orElseThrow(() ->
                        new DataIsNotCorrectException("База данных повреждена, настройки по умолчанию не найдены")));

        Weekday weekday = Weekday.values()[LocalDate.now().getDayOfWeek().getValue() - 1];

        SensorPeriod period = sensorPeriodService.getPeriodByWeekday(weekday, false).orElseGet(
                () -> sensorPeriodService.getPeriodByWeekday(weekday, true).orElseThrow(() ->
                        new DataIsNotCorrectException("База данных повреждена, настройки по умолчанию не найдены")));

        return SensorSetup.builder()
                .serial(obtainedSensor.getSerial())
                .startAt(period.getStartAt())
                .endAt(period.getEndAt())
                .minimalTemperature(settings.getMinimalTemperature())
                .optimalTemperature(settings.getOptimalTemperature())
                .build();
    }
}
