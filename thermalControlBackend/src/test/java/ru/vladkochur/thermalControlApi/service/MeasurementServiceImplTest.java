package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MeasurementRepository;
import ru.vladkochur.thermalControlApi.telegramBot.ThermalControlBot;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceImplTest {
    @Mock
    private MeasurementRepository measurementRepository;
    @Mock
    private SensorServiceImpl sensorService;
    @Mock
    private ThermalControlBot thermalControlBot;
    @InjectMocks
    private MeasurementServiceImpl measurementService;

    private final Sensor sensor = DataUtils.getKostromaSensorPersisted();


    @Test
    @DisplayName("Test get all measurements functionality")
    public void givenThreeMeasurementsInDB_whenFindAll_thenRepositoryIsCalled() {
        //given
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(measurementRepository.findAll()).willReturn(measurements);
        //when
        List<Measurement> obtainedMeasurements = measurementService.findAll();
        //then
        assertThat(obtainedMeasurements).isNotEmpty();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test get last week measurements functionality")
    public void givenThreeMeasurementsInDB_whenFindLastWeekMeasurements_thenRepositoryIsCalled() {
        //given
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(measurementRepository.findLastWeekMeasurements()).willReturn(measurements);
        //when
        List<Measurement> obtainedMeasurements = measurementService.findLastWeekMeasurements();
        //then
        assertThat(obtainedMeasurements).isNotEmpty();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).findLastWeekMeasurements();
    }

    @Test
    @DisplayName("Test get all measurements by sensor serial functionality")
    public void givenThreeMeasurementsInDB_whenFindAllBySensorSerial_thenRepositoryIsCalled() {
        //given
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(measurementRepository
                .findMeasurementsBySensor_Serial(anyInt())).willReturn(measurements);
        //when
        List<Measurement> obtainedMeasurements = measurementService.findAllMeasurementsBySensorSerial(sensor.getSerial());
        //then
        assertThat(obtainedMeasurements).isNotEmpty();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).findMeasurementsBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test get all measurements by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindAllBySensorSerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                measurementService.findAllMeasurementsBySensorSerial(sensor.getSerial()));
        //then
        verify(measurementRepository, BDDMockito.never()).findMeasurementsBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test get last week measurements by sensor serial functionality")
    public void givenThreeMeasurementsInDB_whenFindLastWeekMeasurementsBySensorSerial_thenRepositoryIsCalled() {
        //given
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(measurementRepository
                .findLastWeekMeasurementsBySensor_Serial(anyInt())).willReturn(measurements);
        //when
        List<Measurement> obtainedMeasurements = measurementService
                .findLastWeekMeasurementsBySensorSerial(sensor.getSerial());
        //then
        assertThat(obtainedMeasurements).isNotEmpty();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .findLastWeekMeasurementsBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test get last week measurements by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindLastWeekMeasurementsBySensorSerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                measurementService.findAllMeasurementsBySensorSerial(sensor.getSerial()));
        //then
        verify(measurementRepository, BDDMockito.never()).findLastWeekAvgTemperatureBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test get daily measurements by sensor serial functionality")
    public void givenThreeMeasurementsInDB_whenFindDailyMeasurementsMeasurementsBySensorSerial_thenRepositoryIsCalled() {
        //given
        List<Measurement> measurements = DataUtils.getMeasurementsPersisted(sensor);
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(measurementRepository
                .findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(anyInt(),
                        any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(measurements);
        //when
        List<Measurement> obtainedMeasurements = measurementService
                .findDailyMeasurementsBySensorSerial(sensor.getSerial());
        //then
        assertThat(obtainedMeasurements).isNotEmpty();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(anyInt(),
                        any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Test get daily measurements by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindDailyMeasurementsMeasurementsBySensorSerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                measurementService.findDailyMeasurementsBySensorSerial(sensor.getSerial()));
        //then
        verify(measurementRepository, BDDMockito.never())
                .findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(anyInt(),
                        any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Test find average measurement functionality")
    public void givenThreeMeasurementsInDB_whenFindAvgMeasurement_thenRepositoryIsCalled() {
        //given
        BDDMockito.given(measurementRepository.getRowCount()).willReturn(3);
        //when
        Measurement avg = measurementService.findAvgMeasurement().orElse(null);
        //then
        assertThat(avg).isNotNull();
        assertThat(avg.getTime()).isNotNull();
        assertThat(avg.getSensor().getSerial()).isEqualTo(-1);
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).getRowCount();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).findLastWeekAvgTemperature();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).findLastWeekAvgHumidity();
    }

    @Test
    @DisplayName("Test find average measurement with empty DB functionality")
    public void givenEmptyDB_whenFindAvgMeasurement_thenRepositoryIsCalled() {
        //given
        BDDMockito.given(measurementRepository.getRowCount()).willReturn(0);
        //when
        Measurement avg = measurementService.findAvgMeasurement().orElse(null);
        //then
        assertThat(avg).isNull();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).getRowCount();
        BDDMockito.verify(measurementRepository, BDDMockito.times(0)).findLastWeekAvgTemperature();
        BDDMockito.verify(measurementRepository, BDDMockito.times(0)).findLastWeekAvgHumidity();
    }

    @Test
    @DisplayName("Test find average measurement by serial functionality")
    public void givenThreeMeasurementsInDB_whenFindAvgMeasurementBySensorSerial_thenRepositoryIsCalled() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(measurementRepository.countBySensorSerial(sensor.getSerial())).willReturn(3);
        //when
        Measurement avg = measurementService.findAvgMeasurementBySensorSerial(sensor.getSerial()).orElse(null);
        //then
        assertThat(avg).isNotNull();
        assertThat(avg.getTime()).isNotNull();
        assertThat(avg.getSensor().getSerial()).isEqualTo(sensor.getSerial());
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .countBySensorSerial(anyInt());
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .findLastWeekAvgTemperatureBySensor_Serial(anyInt());
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .findLastWeekAvgHumidityBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test find average measurement by serial with empty DB functionality")
    public void givenEmptyDB_whenFindAvgMeasurementBySensorSerial_thenRepositoryIsCalled() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(measurementRepository.countBySensorSerial(sensor.getSerial())).willReturn(0);
        //when
        Measurement avg = measurementService.findAvgMeasurementBySensorSerial(sensor.getSerial()).orElse(null);
        //then
        assertThat(avg).isNull();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .countBySensorSerial(anyInt());
        BDDMockito.verify(measurementRepository, BDDMockito.times(0))
                .findLastWeekAvgTemperatureBySensor_Serial(anyInt());
        BDDMockito.verify(measurementRepository, BDDMockito.times(0))
                .findLastWeekAvgHumidityBySensor_Serial(anyInt());
    }

    @Test
    @DisplayName("Test find average measurement by incorrect serial functionality")
    public void givenIncorrectSerial_whenFindAvgMeasurementBySensorSerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                measurementService.findAllMeasurementsBySensorSerial(sensor.getSerial()));
        //then
        verify(measurementRepository, BDDMockito.never()).findMeasurementsBySensor_Serial(anyInt());
    }


    @Test
    @DisplayName("Test save measurement functionality")
    public void givenMeasurementToSave_whenSaveMeasurement_thenRepositoryIsCalled() {
        //given
        Measurement measurement = DataUtils.getFirstMeasurementPersisted(sensor);
        BDDMockito.given(measurementRepository.save(any(Measurement.class))).willReturn(measurement);
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.of(sensor));
        //when
        Measurement obtainedMeasurement = measurementService.save(DataUtils.getFirstMeasurementTransient(sensor));
        //then
        assertThat(obtainedMeasurement).isNotNull();
        BDDMockito.verify(measurementRepository, BDDMockito.times(1)).save(any(Measurement.class));
    }

    @Test
    @DisplayName("Test send critical measurement before time functionality")
    public void givenCriticalMeasurement_whenSendCriticalMeasurementToAllUsers_thenTgSendMessageToAllUsersIsCalled() {
        //given
        Measurement measurement1 = DataUtils.getFirstCriticalMeasurementTransient(sensor);
        Measurement measurement2 = DataUtils.getSecondCriticalMeasurementTransient(sensor);
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.of(sensor));
        //when
        measurementService.sendCriticalMeasurementToAllUsers(measurement1);
        measurementService.sendCriticalMeasurementToAllUsers(measurement2);
        //then
        BDDMockito.verify(thermalControlBot, BDDMockito.times(2))
                .sendMessageToAllUsers(anyString());
    }

    @Test
    @DisplayName("Test delete measurement before time functionality")
    public void givenDeleteRequest_whenDeleteMeasurementsByTimeBefore_thenRepositoryIsCalled() {
        //given

        //when
        measurementService.deleteMeasurementsByTimeBefore(LocalDateTime.now());
        //then
        BDDMockito.verify(measurementRepository, BDDMockito.times(1))
                .deleteByTimeBefore(any(LocalDateTime.class));
    }

}