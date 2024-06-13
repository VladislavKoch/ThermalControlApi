package ru.vladkochur.thermalControlApi.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SensorInteractionDAO {
    private final JdbcTemplate jdbcTemplate;

    public List<Sensor> findAllSensorsThatWantInteract() {
        return jdbcTemplate.query("Select * from sensor_interaction", new BeanPropertyRowMapper<>(Sensor.class));
    }

    public void makeSensorWantedToInteract(Integer serial) {
        jdbcTemplate.update("insert into sensor_interaction (serial) values (?)", serial);
    }

    public void deleteAll() {
        jdbcTemplate.update("truncate table sensor_interaction");
    }
}
