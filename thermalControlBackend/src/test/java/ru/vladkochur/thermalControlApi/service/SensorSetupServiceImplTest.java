package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;
import ru.vladkochur.thermalControlApi.entity.SensorSetup;
import ru.vladkochur.thermalControlApi.exception.DataIsNotCorrectException;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorSetupServiceImplTest {
    @Mock
    private SensorSettingsServiceImpl settingsService;
    @Mock
    private SensorPeriodServiceImpl periodService;
    @Mock
    private SensorServiceImpl sensorService;
    @InjectMocks
    private SensorSetupServiceImpl setupService;

    @Test
    @DisplayName("Test get settings setup by serial functionality")
    public void givenSensorSerial_whenSensorSetupBySerial_thenRepositoryIsCalled() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));

        SensorSettings setting = DataUtils.getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsService.getSettingsBySerial(anyInt())).willReturn(Optional.ofNullable(setting));

        SensorPeriod period = DataUtils.getSecondSensorPeriodPersisted(Weekday.FRIDAY);
        BDDMockito.given(periodService.getPeriodByWeekday(any(Weekday.class), anyBoolean()))
                .willReturn(Optional.ofNullable(period));
        //when
        SensorSetup obtainedSetup = setupService.getSetupBySerial(1);
        //then
        assertThat(obtainedSetup).isNotNull();
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getSettingsBySerial(anyInt());
        verify(periodService, BDDMockito.times(1)).getPeriodByWeekday(any(Weekday.class),
                anyBoolean());
    }

    @Test
    @DisplayName("Test get settings setup by incorrect serial functionality")
    public void givenIncorrectSerial_whenSensorSetupBySerial_thenExceptionIsThrown() {
        //given
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(SensorNotFoundException.class, () -> setupService.getSetupBySerial(1));
        //then
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
        verify(settingsService, BDDMockito.never()).getSettingsBySerial(anyInt());
        verify(periodService, BDDMockito.never()).getPeriodByWeekday(any(Weekday.class), anyBoolean());
    }

    @Test
    @DisplayName("Test get settings setup by serial with defaults functionality")
    public void givenSerialWithDefaults_whenSensorSetupBySerial_thenRepositoryIsCalled() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));

        SensorSettings setting = DataUtils.getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsService.getSettingsBySerial(anyInt())).willReturn(Optional.empty());
        BDDMockito.given(settingsService.getDefaultSettings()).willReturn(Optional.ofNullable(setting));

        SensorPeriod period = DataUtils.getSecondSensorPeriodPersisted(Weekday.FRIDAY);
        BDDMockito.given(periodService.getPeriodByWeekday(any(Weekday.class), eq(false)))
                .willReturn(Optional.empty());
        BDDMockito.given(periodService.getPeriodByWeekday(any(Weekday.class), eq(true)))
                .willReturn(Optional.ofNullable(period));
        //when
        SensorSetup obtainedSetup = setupService.getSetupBySerial(1);
        //then
        assertThat(obtainedSetup).isNotNull();
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getSettingsBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getDefaultSettings();
        verify(periodService, BDDMockito.times(2)).getPeriodByWeekday(any(Weekday.class),
                anyBoolean());
    }

    @Test
    @DisplayName("Test get settings setup by serial with damaged settings DB functionality")
    public void givenDamagedSettingsDB_whenSensorSetupBySerial_thenExceptionIsThrown() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        BDDMockito.given(settingsService.getSettingsBySerial(anyInt())).willReturn(Optional.empty());
        BDDMockito.given(settingsService.getDefaultSettings()).willReturn(Optional.empty());
        //when
        assertThrows(DataIsNotCorrectException.class, () -> setupService.getSetupBySerial(1));
        //then
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getSettingsBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getDefaultSettings();
        verify(periodService, BDDMockito.never()).getPeriodByWeekday(any(Weekday.class),
                anyBoolean());
    }

    @Test
    @DisplayName("Test get settings setup by serial with damaged periods DB functionality")
    public void givenDamagedPeriodsDB_whenSensorSetupBySerial_thenExceptionIsThrown() {
        //given
        Sensor sensor = DataUtils.getYaroslavlSensorPersisted();
        BDDMockito.given(sensorService.findBySerial(anyInt())).willReturn(Optional.ofNullable(sensor));
        SensorSettings setting = DataUtils.getFirstSensorSettingsPersisted(sensor);
        BDDMockito.given(settingsService.getSettingsBySerial(anyInt())).willReturn(Optional.ofNullable(setting));
        BDDMockito.given(periodService.getPeriodByWeekday(any(Weekday.class), anyBoolean()))
                .willReturn(Optional.empty());
        //when
        assertThrows(DataIsNotCorrectException.class, () -> setupService.getSetupBySerial(1));
        //then
        verify(sensorService, BDDMockito.times(1)).findBySerial(anyInt());
        verify(settingsService, BDDMockito.times(1)).getSettingsBySerial(anyInt());
        verify(settingsService, BDDMockito.never()).getDefaultSettings();
        verify(periodService, BDDMockito.times(2)).getPeriodByWeekday(any(Weekday.class),
                anyBoolean());
    }
}