package ru.vladkochur.thermalControlApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.vladkochur.thermalControlApi.util.DataUtils.getKostromaSensorTransient;

@DataJpaTest
class SensorRepositoryTest {
    @Autowired
    private SensorRepository sensorRepository;

    @BeforeEach
    public void setUp() {
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find sensor by serial functionality")
    public void givenSensorSerial_whenFindSensorBySerial_thenSensorOptionalIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        //when
        Sensor obtainedSensor = sensorRepository.findBySerial(sensor.getSerial()).orElse(null);
        //then
        assertThat(obtainedSensor).isNotNull();
        assertThat(obtainedSensor.getSerial()).isEqualTo(sensor.getSerial());
    }

    @Test
    @DisplayName("Test delete sensor by serial functionality")
    public void givenSensorSerial_whenDeleteBySerial_thenDeveloperIsRemovedFromDB() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        //when
        sensorRepository.deleteSensorBySerial(sensor.getSerial());
        //then
        assertThat(sensorRepository.findAll()).isEmpty();
    }

}