package ru.vladkochur.thermalControlApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MeasurementRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.MeasurementService;
import ru.vladkochur.thermalControlApi.telegramBot.ThermalControlBot;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static ru.vladkochur.thermalControlApi.constants.TelegramMenuEnum.*;


@Service
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorServiceImpl sensorService;
    private final ThermalControlBot thermalControlBot;

    public MeasurementServiceImpl(@Autowired MeasurementRepository measurementRepository,
                                  @Autowired SensorServiceImpl sensorService,
                                  @Lazy ThermalControlBot thermalControlBot) {
        this.measurementRepository = measurementRepository;
        this.sensorService = sensorService;
        this.thermalControlBot = thermalControlBot;
    }

    @Override
    public List<Measurement> findAll() {
        return measurementRepository.findAll();
    }

    @Override
    public List<Measurement> findLastWeekMeasurements() {
        return measurementRepository.findLastWeekMeasurements();
    }

    @Override
    public List<Measurement> findAllMeasurementsBySensorSerial(Integer serial) {
        Sensor obtainedSensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
        return measurementRepository.findMeasurementsBySensor_Serial(obtainedSensor.getSerial());
    }

    @Override
    public List<Measurement> findLastWeekMeasurementsBySensorSerial(Integer serial) {
        Sensor obtainedSensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
        return measurementRepository.findLastWeekMeasurementsBySensor_Serial(obtainedSensor.getSerial());
    }

    @Override
    public List<Measurement> findDailyMeasurementsBySensorSerial(Integer serial) {
        Sensor obtainedSensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
        LocalDateTime ldtNow = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        return measurementRepository.findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(
                obtainedSensor.getSerial(), ldtNow, ldtNow.plusDays(1));
    }

    @Override
    public Optional<Measurement> findAvgMeasurement() {
        if (measurementRepository.getRowCount() <= 0) {
            return Optional.empty();
        }
        double temperature = measurementRepository.findLastWeekAvgTemperature();
        double humidity = measurementRepository.findLastWeekAvgHumidity();
        Sensor sensor = Sensor.builder().name("All sensors").serial(-1).build();
        return Optional.of(Measurement.builder()
                .temperature(Math.floor(temperature * 10) / 10)
                .humidity(Math.floor(humidity * 10) / 10)
                .time(LocalDateTime.now())
                .sensor(sensor)
                .build());
    }

    @Override
    public Optional<Measurement> findAvgMeasurementBySensorSerial(Integer serial) {
        Sensor obtainedSensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
        if (measurementRepository.countBySensorSerial(serial) <= 0) {
            return Optional.empty();
        }
        double temperature = measurementRepository.findLastWeekAvgTemperatureBySensor_Serial(serial);
        double humidity = measurementRepository.findLastWeekAvgHumidityBySensor_Serial(serial);
        return Optional.of(Measurement.builder()
                .temperature(Math.floor(temperature * 10) / 10)
                .humidity(Math.floor(humidity * 10) / 10)
                .time(LocalDateTime.now())
                .sensor(obtainedSensor)
                .build());
    }

    @Override
    public void deleteMeasurementsByTimeBefore(LocalDateTime time) {
        measurementRepository.deleteByTimeBefore(time);
    }

    @Override
    public Measurement save(Measurement measurement) {
        enrichMeasurement(measurement);
        return measurementRepository.save(measurement);
    }

    @Override
    public void sendCriticalMeasurementToAllUsers(Measurement measurement) {
        enrichMeasurement(measurement);
        Sensor sensor = measurement.getSensor();
        String name = sensor.getName() == null ? sensor.getSerial().toString() : sensor.getName();
        String warning = measurement.getTemperature() > 60 ? CRITICAL_TEMPERATURE_HIGH.getMessage() :
                measurement.getTemperature() <= 5 ? CRITICAL_TEMPERATURE_LOW.getMessage() :
                        CRITICAL_TEMPERATURE_ERROR.getMessage();
        String message = String.format("%s\n%s\nТемпература : %s", name, warning,
                (Math.floor(measurement.getTemperature() * 10) / 10));
        thermalControlBot.sendMessageToAllUsers(message);
    }

    private void enrichMeasurement(Measurement measurement) {
        measurement.setSensor(sensorService.findBySerial(measurement.getSensor().getSerial())
                .orElseThrow(SensorNotFoundException::new));
    }
}

