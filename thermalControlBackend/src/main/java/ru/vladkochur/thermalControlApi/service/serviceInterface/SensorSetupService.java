package ru.vladkochur.thermalControlApi.service.serviceInterface;

import ru.vladkochur.thermalControlApi.entity.SensorSetup;

public interface SensorSetupService {
    public SensorSetup getSetupBySerial(Integer serial) ;
    }
