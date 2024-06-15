package ru.vladkochur.thermalControlApi.repository;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@DataJpaTest
class MeasurementRepositoryTest {
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private SensorRepository sensorRepository;


    @BeforeEach
    public void setUp() {
        measurementRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find measurements by sensor serial functionality")
    public void givenSensorSerial_whenFindMeasurementsBySensorSerial_thenMeasurementsAreReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        Sensor sensor2 =  getYaroslavlSensorTransient();
        sensorRepository.save(sensor);
        sensorRepository.save(sensor2);
        measurementRepository.save( getFirstMeasurementTransient(sensor));
        measurementRepository.save( getSecondMeasurementTransient(sensor));
        measurementRepository.save( getThirdMeasurementTransient(sensor2));
        //when
        List<Measurement> obtainedMeasurements =
                measurementRepository.findMeasurementsBySensor_Serial(sensor.getSerial());
        //then
        assertThat(obtainedMeasurements).isNotNull();
        assertThat(obtainedMeasurements).isNotEmpty();
        assertThat(obtainedMeasurements).hasSize(2);
    }

    @Test
    @DisplayName("Test find last week avg temperature by sensor serial functionality")
    public void givenSensorSerial_whenFindLastWeekAvgTemperatureBySensorSerial_thenAvgTemperatureIsReturned() {
        //given
        Sensor sensor1 =  getKostromaSensorTransient();
        sensorRepository.save(sensor1);
        Sensor sensor2 =  getIvanovoSensorTransient();
        sensorRepository.save(sensor2);
        Measurement m1 =  getFirstMeasurementTransient(sensor1);
        Measurement m2 =  getSecondMeasurementTransient(sensor1);
        Measurement m3 =  getSecondMeasurementTransient(sensor2);

        measurementRepository.save(m1);
        measurementRepository.save(m2);
        measurementRepository.save(m3);
        measurementRepository.save( getThirdMeasurementTransient(sensor1));
        //when
        Double temperature = measurementRepository.findLastWeekAvgTemperatureBySensor_Serial(sensor1.getSerial());
        //then
        assertThat(temperature).isNotNull();
        assertThat(temperature).isCloseTo((m1.getTemperature() + m2.getTemperature()) / 2,
                Percentage.withPercentage(0.0001));
    }

    @Test
    @DisplayName("Test find last week avg humidity by sensor serial functionality")
    public void givenSensorSerial_whenFindLastWeekAvgHumidityBySensorSerial_thenAvgTemperatureIsReturned() {
        //given
        Sensor sensor1 =  getKostromaSensorTransient();
        sensorRepository.save(sensor1);
        Sensor sensor2 =  getIvanovoSensorTransient();
        sensorRepository.save(sensor2);
        Measurement m1 =  getFirstMeasurementTransient(sensor1);
        Measurement m2 =  getSecondMeasurementTransient(sensor1);
        Measurement m3 =  getSecondMeasurementTransient(sensor2);

        measurementRepository.save(m1);
        measurementRepository.save(m2);
        measurementRepository.save(m3);
        measurementRepository.save( getThirdMeasurementTransient(sensor1));
        //when
        Double temperature = measurementRepository.findLastWeekAvgHumidityBySensor_Serial(sensor1.getSerial());
        //then
        assertThat(temperature).isNotNull();
        assertThat(temperature).isCloseTo((m1.getHumidity()+m2.getHumidity())/2,
                Percentage.withPercentage(0.0001));
    }


    @Test
    @DisplayName("Test find last week avg temperature functionality")
    public void givenSensorSerial_whenFindLastWeekAvgTemperature_thenAvgTemperatureIsReturned() {
        //given
        Sensor sensor1 =  getKostromaSensorTransient();
        sensorRepository.save(sensor1);
        Sensor sensor2 =  getIvanovoSensorTransient();
        sensorRepository.save(sensor2);
        Measurement m1 =  getFirstMeasurementTransient(sensor1);
        Measurement m2 =  getSecondMeasurementTransient(sensor1);
        Measurement m3 =  getSecondMeasurementTransient(sensor2);

        measurementRepository.save(m1);
        measurementRepository.save(m2);
        measurementRepository.save(m3);
        measurementRepository.save( getThirdMeasurementTransient(sensor1));
        //when
        Double temperature = measurementRepository.findLastWeekAvgTemperature();
        //then
        assertThat(temperature).isNotNull();
        assertThat(temperature).isCloseTo((m1.getTemperature() + m2.getTemperature() + m3.getTemperature()) / 3,
                Percentage.withPercentage(0.0001));
    }

    @Test
    @DisplayName("Test find last week avg humidity functionality")
    public void givenSensorSerial_whenFindLastWeekAvgHumidity_thenAvgTemperatureIsReturned() {
        Sensor sensor1 =  getKostromaSensorTransient();
        sensorRepository.save(sensor1);
        Sensor sensor2 =  getIvanovoSensorTransient();
        sensorRepository.save(sensor2);
        Measurement m1 =  getFirstMeasurementTransient(sensor1);
        Measurement m2 =  getSecondMeasurementTransient(sensor1);
        Measurement m3 =  getSecondMeasurementTransient(sensor2);

        measurementRepository.save(m1);
        measurementRepository.save(m2);
        measurementRepository.save(m3);
        measurementRepository.save( getThirdMeasurementTransient(sensor1));
        //when
        Double temperature = measurementRepository.findLastWeekAvgHumidity();
        //then
        assertThat(temperature).isNotNull();
        assertThat(temperature).isCloseTo((m1.getHumidity() + m2.getHumidity() + m3.getHumidity()) / 3,
                Percentage.withPercentage(0.0001));
    }

    @Test
    @DisplayName("Test delete measurements by time before functionality")
    public void givenThreeSensorsInDB_whenDeleteByTimeBefore_thenOneMeasurementIsDeleted() {
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        Measurement m1 =  getFirstMeasurementTransient(sensor);
        Measurement m2 =  getSecondMeasurementTransient(sensor);
        Measurement m3 =  getThirdMeasurementPersisted(sensor);
        measurementRepository.save(m1);
        measurementRepository.save(m2);
        measurementRepository.save(m3);
        //when
        measurementRepository.deleteByTimeBefore(LocalDateTime.now().minusDays(1));
        //then
        List<Measurement>measurements = measurementRepository.findAll();
        assertThat(measurements).isNotNull();
        assertThat(measurements).isNotEmpty();
        assertThat(measurements.size()).isEqualTo(2);
    }
    @Test
    @DisplayName("Test find measurements by sensor serial in the time frame functionality")
    public void
    givenThreeSensorsInDB_whenFindMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime_thenOneMeasurementIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        Measurement m =  getFirstMeasurementTransient(sensor);
        m.setTime(LocalDateTime.now().plusDays(10));
        measurementRepository.save(m);
        measurementRepository.save( getSecondMeasurementTransient(sensor));
        measurementRepository.save( getThirdMeasurementTransient(sensor));
        //when
        List<Measurement> obtainedMeasurements =
                measurementRepository.findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(
                        sensor.getSerial(), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        //then
        assertThat(obtainedMeasurements).isNotNull();
        assertThat(obtainedMeasurements).isNotEmpty();
        assertThat(obtainedMeasurements).hasSize(1);
    }

    @Test
    @DisplayName("Test get row count functionality")
    public void
    givenThreeSensorsInDB_whenGetRowCount_thenRowCountIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        measurementRepository.save( getFirstMeasurementTransient(sensor));
        measurementRepository.save( getSecondMeasurementTransient(sensor));
        measurementRepository.save( getThirdMeasurementTransient(sensor));
        //when
        Integer count = measurementRepository.getRowCount();
        //then
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Test get row count by sensor serial functionality")
    public void
    givenThreeSensorsInDB_whenGetRowCountBySensorSerial_thenRowCountIsReturned() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        sensorRepository.save(sensor);
        measurementRepository.save( getFirstMeasurementTransient(sensor));
        measurementRepository.save( getSecondMeasurementTransient(sensor));
        measurementRepository.save( getThirdMeasurementTransient(sensor));
        //when
        Integer count = measurementRepository.countBySensorSerial(sensor.getSerial());
        //then
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(3);
    }
}

//findLastWeekMeasurementsBySensor_Serial and findLastWeekMeasurements is not compatible with H2