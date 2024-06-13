package ru.vladkochur.thermalControlApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SensorPeriodRepositoryTest {
    @Autowired
    private SensorPeriodRepository sensorPeriodRepository;

    @BeforeEach
    public void setUp() {
        sensorPeriodRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find all periods ordered by weekday functionality")
    public void givenThreePeriodsInDB_whenFindAllOrderByWeekday_thenOrderedPeriodsAreReturned() {
        //given
        sensorPeriodRepository.save(DataUtils.getFirstSensorPeriodTransient(Weekday.SATURDAY));
        sensorPeriodRepository.save(DataUtils.getFirstSensorPeriodTransient(Weekday.MONDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.WEDNESDAY));
        //when
        List<SensorPeriod> periods = sensorPeriodRepository.findAllOrderByWeekday();
        //then
        assertThat(periods).isNotNull();
        assertThat(periods).isNotEmpty();
        assertThat(periods.size()).isEqualTo(3);
        assertThat(periods.get(0).getWeekday()).isEqualTo(Weekday.MONDAY);
        assertThat(periods.get(1).getWeekday()).isEqualTo(Weekday.WEDNESDAY);
        assertThat(periods.get(2).getWeekday()).isEqualTo(Weekday.SATURDAY);
    }

    @Test
    @DisplayName("Test find all not default periods ordered by weekday functionality")
    public void givenThreePeriodsInDB_whenFindAllByIsDefaultFalseOrderByWeekday_thenNotDefaultOrderedPeriodsAreReturned() {
        //given
        sensorPeriodRepository.save(DataUtils.getFirstSensorPeriodTransient(Weekday.SATURDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.MONDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.WEDNESDAY));
        //when
        List<SensorPeriod> periods = sensorPeriodRepository.findAllByIsDefaultFalseOrderByWeekday();
        //then
        assertThat(periods).isNotNull();
        assertThat(periods).isNotEmpty();
        assertThat(periods.size()).isEqualTo(2);
        assertThat(periods.get(0).getWeekday()).isEqualTo(Weekday.MONDAY);
        assertThat(periods.get(1).getWeekday()).isEqualTo(Weekday.WEDNESDAY);
    }

    @Test
    @DisplayName("Test delete all not default periods functionality")
    public void givenThreePeriodsInDB_whenDeleteAllByIsDefaultFalse_thenDeveloperIsRemovedFromDB() {
        //given
        sensorPeriodRepository.save(DataUtils.getFirstSensorPeriodTransient(Weekday.SATURDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.MONDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.WEDNESDAY));
        //when
        sensorPeriodRepository.deleteAllByIsDefaultFalse();
        //then
        List<SensorPeriod>periods = sensorPeriodRepository.findAll();
        assertThat(periods).isNotNull();
        assertThat(periods).isNotEmpty();
        assertThat(periods.size()).isEqualTo(1);
        assertThat(periods.get(0).getWeekday()).isEqualTo(Weekday.SATURDAY);
    }

    @Test
    @DisplayName("Test find period by weekday and default status functionality")
    public void givenThreePeriodsInDB_whenFindByWeekdayAndIsDefault_thenPeriodOptionalIsReturned() {
        //given
        sensorPeriodRepository.save(DataUtils.getFirstSensorPeriodTransient(Weekday.SATURDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.MONDAY));
        sensorPeriodRepository.save(DataUtils.getSecondSensorPeriodTransient(Weekday.SATURDAY));
        //when
        SensorPeriod period = sensorPeriodRepository.findByWeekdayAndIsDefault(Weekday.SATURDAY, false)
                .orElse(null);
        //then
        assertThat(period).isNotNull();
    }

}