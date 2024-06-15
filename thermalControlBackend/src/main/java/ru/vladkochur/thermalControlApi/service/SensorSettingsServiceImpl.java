package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.SensorSettingsRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSettingsService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorSettingsServiceImpl implements SensorSettingsService {

    private final SensorSettingsRepository sensorSettingsRepository;
    private final SensorService sensorService;

    @Override
    public List<SensorSettings> getAllSettings() {
        return sensorSettingsRepository.findAllOrdered();
    }

    @Override
    public Optional<SensorSettings> getSettingsBySerial(Integer serial) {
        return sensorSettingsRepository.findBySensorSerial(serial);
    }

    @Override
    public Optional<SensorSettings> getDefaultSettings() {
        return sensorSettingsRepository.findAllBySensorIsNull();
    }

    @Override
    public void setDefaultSettings(double optimalTemperature, double minimalTemperature) {
        SensorSettings obtainedSettings = sensorSettingsRepository.findAllBySensorIsNull().get();
        obtainedSettings.setOptimalTemperature(optimalTemperature);
        obtainedSettings.setMinimalTemperature(minimalTemperature);
        sensorSettingsRepository.save(obtainedSettings);
    }

    @Override
    public void setSettingsBySerial(double optimalTemperature, double minimalTemperature, int serial) {
        Optional<Sensor> optionalSensor = sensorService.findBySerial(serial);
        if (optionalSensor.isEmpty()) {
            throw new SensorNotFoundException();
        }
            Sensor sensor = optionalSensor.get();
            Optional<SensorSettings> obtainedSettings = sensorSettingsRepository.findBySensorSerial(sensor.getSerial());
            SensorSettings newSettings;
            if (obtainedSettings.isPresent()) {
                newSettings = obtainedSettings.get();
                newSettings.setOptimalTemperature(optimalTemperature);
                newSettings.setMinimalTemperature(minimalTemperature);
            } else {
                newSettings = SensorSettings.builder()
                        .sensor(sensor)
                        .optimalTemperature(optimalTemperature)
                        .minimalTemperature(minimalTemperature)
                        .build();
            }
            sensorSettingsRepository.save(newSettings);
    }

    @Override
    public void deleteSensorSettings(Integer serial) {
        sensorSettingsRepository.deleteSensorSettingsBySensorSerial(serial);
    }

}
