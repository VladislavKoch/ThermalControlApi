package ru.vladkochur.thermalControlApi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;


@Component
@Slf4j
public class MeasurementValidator implements Validator {
    private final SensorService sensorService;

    @Autowired
    public MeasurementValidator(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Measurement.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Measurement measurement = (Measurement) o;
        if (measurement.getSensor() == null) {
            return;
        }
        if (sensorService.findBySerial(measurement.getSensor().getSerial()).isEmpty()) {
            errors.rejectValue("sensor.serial", "", "This sensor is not exist!");
        }
    }
}
