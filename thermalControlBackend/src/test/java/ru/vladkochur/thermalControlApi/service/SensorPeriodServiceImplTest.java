package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.repository.SensorPeriodRepository;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorPeriodServiceImplTest {

    @Mock
    private SensorPeriodRepository sensorPeriodRepository;
    @InjectMocks
    private SensorPeriodServiceImpl sensorPeriodService;

    @Test
    @DisplayName("Test get all periods functionality")
    public void givenTwoPeriods_whenGetAll_thenRepositoryIsCalled() {
        //given
        List<SensorPeriod> periods = List.of(DataUtils.getFirstSensorPeriodPersisted(Weekday.MONDAY),
                DataUtils.getSecondSensorPeriodPersisted(Weekday.FRIDAY));
        BDDMockito.given(sensorPeriodRepository.findAllOrderByWeekday()).willReturn(periods);
        //when
        List<SensorPeriod> obtainedPeriods = sensorPeriodService.getAll();
        //then
        assertThat(obtainedPeriods).isNotEmpty();
        verify(sensorPeriodRepository, BDDMockito.times(1)).findAllOrderByWeekday();
    }

    @Test
    @DisplayName("Test get actual periods functionality")
    public void givenTwoActualPeriods_whenGetActual_thenRepositoryIsCalled() {
        //given
        List<SensorPeriod> periods = List.of(DataUtils.getFirstSensorPeriodPersisted(Weekday.MONDAY),
                DataUtils.getFirstSensorPeriodPersisted(Weekday.FRIDAY));
        BDDMockito.given(sensorPeriodRepository.findAllByIsDefaultFalseOrderByWeekday()).willReturn(periods);
        //when
        List<SensorPeriod> obtainedPeriods = sensorPeriodService.getActual();
        //then
        assertThat(obtainedPeriods).isNotEmpty();
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .findAllByIsDefaultFalseOrderByWeekday();
    }

    @Test
    @DisplayName("Test get period by weekday and default status functionality")
    public void givenPeriod_whenGetPeriodByWeekday_thenRepositoryIsCalled() {
        //given
        SensorPeriod period = DataUtils.getFirstSensorPeriodPersisted(Weekday.MONDAY);
        BDDMockito.given(sensorPeriodRepository.findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean()))
                .willReturn(Optional.ofNullable(period));
        //when
        SensorPeriod obtainedPeriod = sensorPeriodService.getPeriodByWeekday(Weekday.MONDAY, true)
                .orElse(null);
        //then
        assertThat(obtainedPeriod).isNotNull();
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean());
    }

    @Test
    @DisplayName("Test delete actual periods functionality")
    public void givenDeleteActualRequest_whenDeleteAllByIsDefaultFalse_thenRepositoryIsCalled() {
        //given

        //when
        sensorPeriodService.deleteAllActual();
        //then
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .deleteAllByIsDefaultFalse();
    }

    @Test
    @DisplayName("Test set already existed period functionality")
    public void givenPeriod_whenSetOptimalTemperaturePeriod_thenRepositoryIsCalled() {
        //given
        SensorPeriod period = DataUtils.getFirstSensorPeriodPersisted(Weekday.MONDAY);
        SensorPeriod period1 = DataUtils.getFirstSensorPeriodTransient(Weekday.MONDAY);
        BDDMockito.given(sensorPeriodRepository.findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean()))
                .willReturn(Optional.ofNullable(period1));
        BDDMockito.given(sensorPeriodRepository.save(any(SensorPeriod.class))).willReturn(period);
        //when
        SensorPeriod obtainedPeriod = sensorPeriodService.setOptimalTemperaturePeriod(
                LocalTime.of(8,0), LocalTime.of(16, 0), Weekday.MONDAY, true);
        //then
        assertThat(obtainedPeriod).isNotNull();
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean());
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .save(any(SensorPeriod.class));
    }

    @Test
    @DisplayName("Test set not existed period functionality")
    public void givenSetRequest_whenSetOptimalTemperaturePeriod_thenRepositoryIsCalled() {
        //given
        SensorPeriod period = DataUtils.getFirstSensorPeriodTransient(Weekday.MONDAY);
        BDDMockito.given(sensorPeriodRepository.findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean()))
                .willReturn(Optional.empty());
        BDDMockito.given(sensorPeriodRepository.save(any(SensorPeriod.class))).willReturn(period);
        //when
        SensorPeriod obtainedPeriod = sensorPeriodService.setOptimalTemperaturePeriod(
                LocalTime.of(8,0), LocalTime.of(16, 0), Weekday.MONDAY, true);
        //then
        assertThat(obtainedPeriod).isNotNull();
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .findByWeekdayAndIsDefault(any(Weekday.class), anyBoolean());
        verify(sensorPeriodRepository, BDDMockito.times(1))
                .save(any(SensorPeriod.class));
    }

}