package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;

import java.util.List;
import java.util.Optional;

public interface SensorSettingsService {

    public List<SensorSettings> getAllSettings();

    Optional<SensorSettings> getSettingsBySerial(Integer serial);

    Optional<SensorSettings> getDefaultSettings();

    @Transactional
    void setDefaultSettings(double optimalTemperature, double minimalTemperature);

    @Transactional
    void setSettingsBySerial(double optimalTemperature, double minimalTemperature, int serial);

    public void deleteSensorSettings(Integer serial);

}
