package ru.vladkochur.thermalControlApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladkochur.thermalControlApi.entity.Measurement;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

    @Query(value = """
            SELECT row_number() OVER () AS id,
                   time,
                   AVG(temperature) AS temperature,
                   AVG(humidity) AS humidity,
                   AVG(sensor) AS sensor
            FROM (
                SELECT date_trunc('hour', time) - (EXTRACT(HOUR FROM time) % 3) * interval '1 hour' AS time,
                       temperature,
                       humidity,
                       sensor
                FROM measurement
                WHERE time >= NOW() - INTERVAL '7 days'
            ) AS subquery
            GROUP BY time
            ORDER BY time;
            """, nativeQuery = true)
    List<Measurement> findLastWeekMeasurements();

    List<Measurement> findMeasurementsBySensor_Serial(Integer serial);

    @Query(value = """
            SELECT row_number() OVER () AS id,
                   time,
                   AVG(temperature) AS temperature,
                   AVG(humidity) AS humidity,
                   AVG(sensor) AS sensor
            FROM (
                SELECT date_trunc('hour', time) - (EXTRACT(HOUR FROM time) % 3) * interval '1 hour' AS time,
                       temperature,
                       humidity,
                       sensor
                FROM measurement
                WHERE sensor = ?1 AND time >= NOW() - INTERVAL '7 days'
            ) AS subquery
            GROUP BY time
            ORDER BY time;
            """, nativeQuery = true)
    List<Measurement> findLastWeekMeasurementsBySensor_Serial(Integer serial);

    @Query(value = "SELECT AVG (temperature) FROM measurement WHERE sensor = ?1 " +
            "AND time BETWEEN NOW() - INTERVAL '7' DAY AND NOW()", nativeQuery = true)
    Double findLastWeekAvgTemperatureBySensor_Serial(Integer serial);

    @Query(value = "SELECT AVG (humidity) FROM measurement WHERE sensor = ?1 " +
            "AND time BETWEEN NOW() - INTERVAL '7' DAY AND NOW()", nativeQuery = true)
    Double findLastWeekAvgHumidityBySensor_Serial(Integer serial);

    @Query(value = "SELECT AVG (temperature) FROM measurement WHERE time BETWEEN NOW() - INTERVAL '7' DAY AND NOW()",
            nativeQuery = true)
    Double findLastWeekAvgTemperature();

    @Query(value = "SELECT AVG (humidity) FROM measurement WHERE time BETWEEN now() - INTERVAL '7' DAY AND NOW()",
            nativeQuery = true)
    Double findLastWeekAvgHumidity();

    void deleteByTimeBefore(LocalDateTime time);

    List<Measurement> findMeasurementsBySensor_SerialAndTimeAfterAndTimeBeforeOrderByTime(
            Integer serial, LocalDateTime after,LocalDateTime before);

    @Query("select count(id) from Measurement")
    Integer getRowCount();

    Integer countBySensorSerial(Integer serial);
}
