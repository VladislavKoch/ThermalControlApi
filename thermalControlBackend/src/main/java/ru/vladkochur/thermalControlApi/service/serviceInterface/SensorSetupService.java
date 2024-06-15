package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.SensorSetup;

@Transactional(readOnly = true)
public interface SensorSetupService {
    public SensorSetup getSetupBySerial(Integer serial) ;
    }
