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
        return jdbcTemplate.query("SELECT * FROM sensor_interaction", new BeanPropertyRowMapper<>(Sensor.class));
    }

    public void makeSensorWantedToInteract(Integer serial) {
        jdbcTemplate.update("INSERT INTO sensor_interaction (serial) VALUES (?)", serial);
    }

    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE TABLE sensor_interaction");
    }
}
