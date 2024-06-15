package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.util.List;
import java.util.Optional;

@Transactional
public interface SensorService {
    @Transactional(readOnly = true)
    public List<Sensor> findAll();

    @Transactional(readOnly = true)
    public Optional<Sensor> findBySerial(Integer serial);

    public Sensor save(Sensor sensor);

    public void deleteBySerial(Integer serial);

    public Sensor update(Sensor updatedSensor);

    public void makeSensorWantedToInteract(Sensor sensor);

    @Transactional(readOnly = true)
    public List<Sensor> findAllSensorsWantedForInteraction();

    public void makeAllSensorsUnwantedToInteract();
}
