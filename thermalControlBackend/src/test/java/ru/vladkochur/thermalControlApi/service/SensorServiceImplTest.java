package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.dao.SensorInteractionDAO;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@ExtendWith(MockitoExtension.class)
class SensorServiceImplTest {
    @Mock
    private SensorRepository sensorRepository;
    @InjectMocks
    private SensorServiceImpl sensorService;
    @Mock
    private SensorInteractionDAO sensorInteractionDAO;

    @Test
    @DisplayName("Test get all sensors functionality")
    public void givenTwoSensors_whenGetAllSensors_thenRepositoryIsCalled() {
        //given
        List<Sensor> sensors = List.of( getKostromaSensorPersisted(),  getYaroslavlSensorPersisted());
        BDDMockito.given(sensorRepository.findAll()).willReturn(sensors);
        //when
        List<Sensor> obtainedSensors = sensorService.findAll();

        //then
        assertThat(obtainedSensors).isNotEmpty();
        verify(sensorRepository, BDDMockito.times(1)).findAll();
    }


    @Test
    @DisplayName("Test get sensor by serial functionality")
    public void givenSensorSerial_whenFindBySerial_thenRepositoryIsCalled() {
        //given
        Sensor sensor =  getKostromaSensorPersisted();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        //when
        Sensor obtainedSensor = sensorService.findBySerial(1).orElse(null);
        //then
        assertThat(obtainedSensor).isNotNull();
        verify(sensorRepository, BDDMockito.times(1)).findBySerial(anyInt());
    }

    @Test
    @DisplayName("Test get sensor by incorrect serial functionality")
    public void givenSensorSerial_whenFindByIncorrectSerial_thenEmptyOptionalIsReturned() {
        //given
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        Optional<Sensor> obtainedSensor = sensorService.findBySerial(1);
        //then
        assertThat(obtainedSensor).isEmpty();
    }

    @Test
    @DisplayName("Test save sensor functionality")
    public void givenSensor_whenSaveSensor_thenRepositoryIsCalled() {
        //given
        Sensor sensor =  getKostromaSensorPersisted();
        BDDMockito.given(sensorRepository.save(any(Sensor.class))).willReturn(sensor);
        //when
        Sensor obtainedSensor = sensorService.save(sensor);
        //then
        assertThat(obtainedSensor).isNotNull();
        verify(sensorRepository, BDDMockito.times(1)).save(any(Sensor.class));
    }

    @Test
    @DisplayName("Test delete sensor by serial functionality")
    public void givenSensor_whenDeleteSensor_thenRepositoryIsCalled() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        //when
        sensorService.deleteBySerial(1);
        //then
        BDDMockito.verify(sensorRepository, BDDMockito.times(1)).findBySerial(anyInt());
        BDDMockito.verify(sensorRepository, BDDMockito.times(1)).deleteSensorBySerial(anyInt());
    }

    @Test
    @DisplayName("Test delete sensor by incorrect serial functionality")
    public void givenIncorrectId_whenDeleteSensor_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                sensorService.deleteBySerial(20));
        //then
        verify(sensorRepository, BDDMockito.never()).deleteSensorBySerial(anyInt());
    }

    @Test
    @DisplayName("Test update sensor functionality")
    public void givenSensorToUpdate_whenUpdateSensor_thenRepositoryIsCalled() {
        //given
        Sensor sensorToUpdate =  getKostromaSensorPersisted();
        Sensor updatedSensor =  getKostromaSensorPersisted();
        updatedSensor.setName("Serednaya");
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.of(sensorToUpdate));
        BDDMockito.given(sensorRepository.save(any(Sensor.class))).willReturn(updatedSensor);
        //when
        Sensor obtainedSensor = sensorService.update(updatedSensor);
        //then
        assertThat(obtainedSensor.getSerial()).isEqualTo(sensorToUpdate.getSerial());
        assertThat(obtainedSensor.getName()).isEqualTo(updatedSensor.getName());
        BDDMockito.verify(sensorRepository, BDDMockito.times(1)).save(any(Sensor.class));
    }

    @Test
    @DisplayName("Test update sensor with incorrect serial functionality")
    public void givenSensorToUpdateWithIncorrectSerial_whenUpdateSensor_thenExceptionIsThrown() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                sensorService.update(sensor));
        //then
        verify(sensorRepository, BDDMockito.never()).deleteSensorBySerial(anyInt());
    }

    @Test
    @DisplayName("Test make sensor wanted to interact functionality")
    public void givenSensorSerial_whenMakeSensorWantedToInteract_thenRepositoryIsCalled() {
        //given
        Sensor sensorThatNotWantToInteract =  getKostromaSensorTransient();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.of(sensorThatNotWantToInteract));
        BDDMockito.given(sensorInteractionDAO.findAllSensorsThatWantInteract()).willReturn(List.of());
        BDDMockito.doNothing().when(sensorInteractionDAO).makeSensorWantedToInteract(anyInt());
        //when
        sensorService.makeSensorWantedToInteract(sensorThatNotWantToInteract);
        //then
        BDDMockito.verify(sensorInteractionDAO,
                BDDMockito.times(1)).makeSensorWantedToInteract(anyInt());
    }

    @Test
    @DisplayName("Test make duplicate sensor wanted to interact functionality")
    public void givenSensorSerialDuplicate_whenMakeSensorWantedToInteract_thenRepositoryIsNotCalled() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.of(sensor));
        BDDMockito.given(sensorInteractionDAO.findAllSensorsThatWantInteract()).willReturn(List.of(sensor));
        //when
        sensorService.makeSensorWantedToInteract(sensor);
        //then
        BDDMockito.verify(sensorInteractionDAO,
                BDDMockito.never()).makeSensorWantedToInteract(anyInt());
    }

    @Test
    @DisplayName("Test make sensor with incorrect serial wanted to interact functionality")
    public void givenIncorrectId_whenSensorWantInteract_thenExceptionIsThrown() {
        //given
        Sensor sensor =  getKostromaSensorTransient();
        BDDMockito.given(sensorRepository.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                sensorService.makeSensorWantedToInteract(sensor));
        //then
        verify(sensorInteractionDAO, BDDMockito.never()).makeSensorWantedToInteract(anyInt());
    }

    @Test
    @DisplayName("Test get all sensors that wanted to interact functionality")
    public void givenTwoSensors_whenFindAllSensorsWantedForInteraction_thenRepositoryIsCalled() {
        //given
        Sensor sensor1 =  getKostromaSensorPersisted();
        Sensor sensor2 =  getYaroslavlSensorPersisted();

        BDDMockito.given(sensorInteractionDAO.findAllSensorsThatWantInteract()).willReturn(List.of(sensor1, sensor2));
        //when
        List<Sensor> obtainedSensors = sensorService.findAllSensorsWantedForInteraction();
        //then
        assertThat(obtainedSensors).isNotEmpty();
        assertThat(obtainedSensors.size()).isEqualTo(2);
        verify(sensorInteractionDAO, BDDMockito.times(1)).findAllSensorsThatWantInteract();
    }

    @Test
    @DisplayName("Test make all sensors unwanted to interact functionality")
    public void givenRepositoryCall_whenMakeAllSensorsUnwantedToInteract_thenRepositoryIsCalled() {
        //given
        BDDMockito.doNothing().when(sensorInteractionDAO).deleteAll();
        //when
        sensorService.makeAllSensorsUnwantedToInteract();
        //then
        verify(sensorInteractionDAO, BDDMockito.times(1)).deleteAll();
    }
}