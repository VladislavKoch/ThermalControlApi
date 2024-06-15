package ru.vladkochur.thermalControlApi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;


@Component
public class SensorValidator implements Validator {
    private final SensorService sensorService;

    @Autowired
    public SensorValidator(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Sensor.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Sensor sensor = (Sensor) o;
        if (sensorService.findBySerial(sensor.getSerial()).isPresent()) {
            errors.rejectValue("serial", "", "This serial is already used");
        }
    }
}
