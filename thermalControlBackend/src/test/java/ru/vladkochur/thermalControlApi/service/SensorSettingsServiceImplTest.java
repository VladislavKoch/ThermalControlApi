package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.repository.SensorSettingsRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@ExtendWith(MockitoExtension.class)
class SensorSettingsServiceImplTest {

    @Mock
    private SensorSettingsRepository settingsRepository;
    @Mock
    private SensorServiceImpl sensorService;
    @InjectMocks
    private SensorSettingsServiceImpl settingsService;

    private final Sensor sensor =  getYaroslavlSensorPersisted();

    @Test
    @DisplayName("Test get all settings functionality")
    public void givenTwoSettings_whenGetAll_thenRepositoryIsCalled() {
        //given
        List<SensorSettings> settings = List.of( getFirstSensorSettingsPersisted(sensor),
                 getFirstSensorSettingsPersisted(sensor));
        BDDMockito.given(settingsRepository.findAllOrdered()).willReturn(settings);
        //when
        List<SensorSettings> obtainedSettings = settingsService.getAllSettings();
        //then
        assertThat(obtainedSettings).isNotEmpty();
        verify(settingsRepository, BDDMockito.times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Test get settings by serial functionality")
    public void givenSensorSerial_whenFindBySerial_thenRepositoryIsCalled() {
        //given
        SensorSettings setting =  getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsRepository.findBySensorSerial(anyInt())).willReturn(Optional.ofNullable(setting));
        //when
        SensorSettings obtainedSetting = settingsService.getSettingsBySerial(1).orElse(null);
        //then
        assertThat(obtainedSetting).isNotNull();
        verify(settingsRepository, BDDMockito.times(1)).findBySensorSerial(anyInt());
    }

    @Test
    @DisplayName("Test get settings by incorrect serial functionality")
    public void givenSensorSerial_whenFindByIncorrectSerial_thenEmptyOptionalIsReturned() {
        //given
        BDDMockito.given(settingsRepository.findBySensorSerial(anyInt())).willReturn(Optional.empty());
        //when
        SensorSettings obtainedSetting = settingsService.getSettingsBySerial(1).orElse(null);
        //then
        assertThat(obtainedSetting).isNull();
        verify(settingsRepository, BDDMockito.times(1)).findBySensorSerial(anyInt());
    }

    @Test
    @DisplayName("Test get default settings functionality")
    public void givenTwoSettings_whenGetDefaultSettings_thenRepositoryIsCalled() {
        //given
        SensorSettings settings =  getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsRepository.findAllBySensorIsNull()).willReturn(Optional.ofNullable(settings));
        //when
        SensorSettings obtainedSettings = settingsService.getDefaultSettings().orElse(null);
        //then
        assertThat(obtainedSettings).isNotNull();
        verify(settingsRepository, BDDMockito.times(1)).findAllBySensorIsNull();
    }

    @Test
    @DisplayName("Test delete settings by serial functionality")
    public void givenDeleteRequest_whenDeleteSensorSettings_thenRepositoryIsCalled() {
        //given

        //when
        settingsService.deleteSensorSettings(1);
        //then
        verify(settingsRepository, BDDMockito.times(1))
                .deleteSensorSettingsBySensorSerial(anyInt());
    }

    @Test
    @DisplayName("Test set already existed settings by serial functionality")
    public void givenExistedSettings_whenSetSettingsBySerial_thenRepositoryIsCalled() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        SensorSettings settings =  getFirstSensorSettingsTransient(sensor);
        SensorSettings settings2 =  getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsRepository.findBySensorSerial(anyInt())).willReturn(Optional.ofNullable(settings));
        BDDMockito.given(settingsRepository.save(any(SensorSettings.class))).willReturn(settings2);
        //when
        settingsService.setSettingsBySerial(22.0, 15.0, sensor.getSerial());
        //then
        verify(settingsRepository, BDDMockito.times(1)).findBySensorSerial(anyInt());
        verify(settingsRepository, BDDMockito.times(1)).save(any(SensorSettings.class));
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
    }

    @Test
    @DisplayName("Test set already existed settings by serial functionality")
    public void givenNotExistedSettings_whenSetSettingsBySerial_thenRepositoryIsCalled() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        SensorSettings settings2 =  getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsRepository.findBySensorSerial(anyInt())).willReturn(Optional.empty());
        BDDMockito.given(settingsRepository.save(any(SensorSettings.class))).willReturn(settings2);
        //when
        settingsService.setSettingsBySerial(22.0, 15.0, sensor.getSerial());
        //then
        verify(settingsRepository, BDDMockito.times(1)).findBySensorSerial(anyInt());
        verify(settingsRepository, BDDMockito.times(1)).save(any(SensorSettings.class));
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
    }

    @Test
    @DisplayName("Test set settings by incorrect serial functionality")
    public void givenIncorrectSerial_whenSetSettingsBySerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () ->
                settingsService.setSettingsBySerial(22.0, 15.0, sensor.getSerial()));
        //then
        verify(settingsRepository, BDDMockito.never()).findBySensorSerial(anyInt());
        verify(settingsRepository, BDDMockito.never()).save(any(SensorSettings.class));
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
    }

    @Test
    @DisplayName("Test set default settings functionality")
    public void givenSettings_whenSetDefaultSettings_thenRepositoryIsCalled() {
        //given
        SensorSettings settings =  getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsRepository.findAllBySensorIsNull()).willReturn(Optional.of(settings));
        BDDMockito.given(settingsRepository.save(any(SensorSettings.class))).willReturn(settings);
        //when
        settingsService.setDefaultSettings(22.0, 15.0);
        //then
        verify(settingsRepository, BDDMockito.times(1)).findAllBySensorIsNull();
        verify(settingsRepository, BDDMockito.times(1)).save(any(SensorSettings.class));
    }
}