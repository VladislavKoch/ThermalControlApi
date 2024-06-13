package ru.vladkochur.thermalControlApi.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Sql(value = {"classpath:daoTestData.sql"} )
@JdbcTest
class InteractionDAOTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SensorInteractionDAO dao;

    @Test
    @DisplayName("Test find all sensors that want to interact functionality")
    public void givenTwoSensorsInDB_whenFindSensorsByWantsInteraction_thenTwoSensorsAreReturned() {
        //given
        dao = new SensorInteractionDAO(jdbcTemplate);
        //when
        List<Sensor> sensors = dao.findAllSensorsThatWantInteract();
        //then
        System.out.println(sensors);
        assertThat(sensors).isNotEmpty();
        assertThat(sensors.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test make sensor wanted to interact functionality")
    public void givenSensorSerial_whenMakeSensorWantedToInteract_thenSensorAreSavedInDB() {
        //given
        dao = new SensorInteractionDAO(jdbcTemplate);
        Sensor sensor = DataUtils.getIvanovoSensorPersisted();
        //when
        dao.makeSensorWantedToInteract(sensor.getSerial());
        //then
        List<Sensor> sensors = dao.findAllSensorsThatWantInteract();
        assertThat(sensors).isNotEmpty();
        assertThat(sensors.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test make sensor wanted to interact functionality")
    public void givenTwoSensorsInDB_whenDeleteAll_thenDbIsEmpty() {
        //given
        dao = new SensorInteractionDAO(jdbcTemplate);
        //when
        dao.deleteAll();
        //then
        List<Sensor> sensors = dao.findAllSensorsThatWantInteract();
        assertThat(sensors).isEmpty();
    }
}