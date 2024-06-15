package ru.vladkochur.thermalControlApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.vladkochur.thermalControlApi.util.DataUtils.getFirstSensorSettingsTransient;
import static ru.vladkochur.thermalControlApi.util.DataUtils.getKostromaSensorTransient;


@DataJpaTest
class SensorSettingsRepositoryTest {
    @Autowired
    private SensorSettingsRepository sensorSettingsRepository;
    @Autowired
    private SensorRepository sensorRepository;

    @BeforeEach
    public void setUp() {
        sensorSettingsRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find all periods ordered functionality")
    public void givenDefaultAndManualSettingsInDB_whenFindAllOrdered_thenOrderedSettingsAreReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(sensor));
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(null));
        //when
        List<SensorSettings> settings = sensorSettingsRepository.findAllOrdered();
        //then
        assertThat(settings).isNotNull();
        assertThat(settings).isNotEmpty();
        assertThat(settings.get(0).getSensor()).isNull();
    }

    @Test
    @DisplayName("Test find all default periods functionality")
    public void givenDefaultAndManualSettingsInDB_whenFindAllBySensorIsNull_thenSettingOptionalIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(sensor));
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(null));
        //when
        SensorSettings setting = sensorSettingsRepository.findAllBySensorIsNull().orElse(null);
        //then
        assertThat(setting).isNotNull();
        assertThat(setting.getSensor()).isNull();
    }

    @Test
    @DisplayName("Test find setting by sensor serial functionality")
    public void givenSensorSerial_whenFindBySensorSerial_thenSettingOptionalIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(sensor));
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(null));
        //when
        SensorSettings setting = sensorSettingsRepository.findBySensorSerial(sensor.getSerial()).orElse(null);
        //then
        assertThat(setting).isNotNull();
        assertThat(setting.getSensor().getSerial()).isEqualTo(sensor.getSerial());
    }

    @Test
    @DisplayName("Test delete settings by serial functionality")
    public void givenSensorSerial_whenDeleteSettingsBySerial_thenSettingsIsRemovedFromDB() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        sensorSettingsRepository.save( getFirstSensorSettingsTransient(sensor));
        //when
        sensorSettingsRepository.deleteSensorSettingsBySensorSerial(sensor.getSerial());
        //then
        assertThat(sensorSettingsRepository.findAll()).isEmpty();
    }

}