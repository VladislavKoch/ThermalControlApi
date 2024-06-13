package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.dao.SensorInteractionDAO;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.SensorRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;
    private final SensorInteractionDAO dao;

    @Override
    public List<Sensor> findAll() {
        return sensorRepository.findAll();
    }

    @Override
    public Optional<Sensor> findBySerial(Integer serial) {
        return sensorRepository.findBySerial(serial);
    }

    @Override
    @Transactional
    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    @Override
    @Transactional
    public void deleteBySerial(Integer serial) {
        Sensor obtainedSensor = sensorRepository.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
        sensorRepository.deleteSensorBySerial(obtainedSensor.getSerial());
    }

    @Override
    @Transactional
    public Sensor update(Sensor updatedSensor) {
        Sensor sensorToUpdate = sensorRepository
                .findBySerial(updatedSensor.getSerial()).orElseThrow(SensorNotFoundException::new);
        sensorToUpdate.setName(updatedSensor.getName());
        return sensorRepository.save(sensorToUpdate);
    }

    @Override
    @Transactional
    public void makeSensorWantedToInteract(Sensor sensor) {
        Sensor obtainedSensor = sensorRepository.findBySerial(sensor.getSerial()).orElseThrow(SensorNotFoundException::new);
        boolean isExist = dao.findAllSensorsThatWantInteract().stream()
                .anyMatch(x -> Objects.equals(x.getSerial(), obtainedSensor.getSerial()));
        if (!isExist) {
            dao.makeSensorWantedToInteract(obtainedSensor.getSerial());
        }
    }

    @Override
    public List<Sensor> findAllSensorsWantedForInteraction() {
        return dao.findAllSensorsThatWantInteract();
    }

    @Override
    @Transactional
    public void makeAllSensorsUnwantedToInteract() {
        dao.deleteAll();
    }

}