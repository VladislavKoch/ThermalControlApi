package ru.vladkochur.thermalControlApi.service.serviceInterface;

import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorService {

    public List<Sensor> findAll();

    public Optional<Sensor> findBySerial(Integer serial);

    public Sensor save(Sensor sensor);

    public void deleteBySerial(Integer serial);

    public Sensor update(Sensor updatedSensor);

    public void makeSensorWantedToInteract(Sensor sensor);

    public List<Sensor> findAllSensorsWantedForInteraction();

    public void makeAllSensorsUnwantedToInteract();
}
