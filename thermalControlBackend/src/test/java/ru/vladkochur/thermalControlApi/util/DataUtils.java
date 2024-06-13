package ru.vladkochur.thermalControlApi.util;

import org.springframework.stereotype.Component;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.dto.MeasurementDTO;
import ru.vladkochur.thermalControlApi.dto.MyUserDTO;
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.dto.SensorSetupDTO;
import ru.vladkochur.thermalControlApi.entity.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataUtils {

    public static Sensor getKostromaSensorTransient() {
        return Sensor.builder()
                .name("Kostroma")
                .serial(156000)
                .build();
    }

    public static Sensor getYaroslavlSensorTransient() {
        return Sensor.builder()
                .name("Yaroslavl")
                .serial(150000)
                .build();
    }

    public static Sensor getKostromaSensorPersisted() {
        return Sensor.builder()
                .id(1)
                .name("Kostroma")
                .serial(156000)
                .build();
    }

    public static Sensor getYaroslavlSensorPersisted() {
        return Sensor.builder()
                .id(2)
                .name("Yaroslavl")
                .serial(150000)
                .build();
    }

    public static Sensor getIvanovoSensorPersisted() {
        return Sensor.builder()
                .id(3)
                .name("Ivanovo")
                .serial(153000)
                .build();
    }

    public static Sensor getIvanovoSensorTransient() {
        return Sensor.builder()
                .name("Ivanovo")
                .serial(153000)
                .build();
    }

    public static MyUser getFirstUserTransient() {
        return MyUser.builder()
                .login("user")
                .roles("ROLE_USER")
                .password("123")
                .telegram("156000")
                .build();
    }

    public static MyUser getSecondUserTransient() {
        return MyUser.builder()
                .login("admin")
                .roles("ROLE_ADMIN")
                .password("123")
                .telegram("158000")
                .build();
    }

    public static MyUser getThirdUserTransient() {
        return MyUser.builder()
                .login("sensor")
                .roles("ROLE_SENSOR")
                .password("123")
                .build();
    }

    public static MyUserDTO getFirstUserDTO() {
        return MyUserDTO.builder()
                .id(1)
                .login("user")
                .roles("ROLE_USER")
                .password("123")
                .telegram("156000")
                .build();
    }

    public static MyUserDTO getSecondUserDTO() {
        return MyUserDTO.builder()
                .id(2)
                .login("admin")
                .roles("ROLE_ADMIN")
                .password("123")
                .telegram("158000")
                .build();
    }

    public static MyUser getFirstUserPersisted() {
        return MyUser.builder()
                .id(1)
                .login("user")
                .roles("ROLE_USER")
                .password("123")
                .telegram("156000")
                .build();
    }

    public static MyUser getSecondUserPersisted() {
        return MyUser.builder()
                .id(2)
                .login("admin")
                .roles("ROLE_ADMIN")
                .password("123")
                .telegram("158000")
                .build();
    }

    public static MyUser getThirdUserPersisted() {
        return MyUser.builder()
                .id(3)
                .login("sensor")
                .roles("ROLE_SENSOR")
                .password("123")
                .build();
    }

    public static SensorPeriod getFirstSensorPeriodTransient(Weekday weekday) {
    return SensorPeriod.builder()
            .startAt(LocalTime.of(8, 0))
            .endAt(LocalTime.of(18, 0))
            .isDefault(true)
            .weekday(weekday)
            .build();
    }

    public static SensorPeriod getSecondSensorPeriodTransient(Weekday weekday) {
        return SensorPeriod.builder()
                .startAt(LocalTime.of(9, 0))
                .endAt(LocalTime.of(16, 30))
                .isDefault(false)
                .weekday(weekday)
                .build();
    }

    public static SensorPeriod getFirstSensorPeriodPersisted(Weekday weekday) {
        return SensorPeriod.builder()
                .id(1)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(18, 0))
                .isDefault(true)
                .weekday(weekday)
                .build();
    }

    public static SensorPeriod getSecondSensorPeriodPersisted(Weekday weekday) {
        return SensorPeriod.builder()
                .id(2)
                .startAt(LocalTime.of(9, 0))
                .endAt(LocalTime.of(16, 30))
                .isDefault(false)
                .weekday(weekday)
                .build();
    }

    public static SensorSettings getFirstSensorSettingsTransient(Sensor sensor) {
        return SensorSettings.builder()
                .minimalTemperature(15.0)
                .optimalTemperature(23.0)
                .sensor(sensor)
                .build();
    }

    public static SensorSettings getFirstSensorSettingsPersisted(Sensor sensor) {
        return SensorSettings.builder()
                .id(1)
                .minimalTemperature(15.0)
                .optimalTemperature(23.0)
                .sensor(sensor)
                .build();
    }

    public static TelegramInteraction getFirstTelegramInteractionTransient() {
        return TelegramInteraction.builder()
                .telegram_id("156000")
                .username("User")
                .time(LocalDateTime.of(2024, 5,1,1,1))
                .build();
    }

    public static TelegramInteraction getFirstTelegramInteractionPersisted() {
        return TelegramInteraction.builder()
                .id(1)
                .telegram_id("156000")
                .username("User")
                .time(LocalDateTime.of(2024, 5,1,1,1))
                .build();
    }

    public static Measurement getFirstMeasurementPersisted(Sensor sensor) {
        return Measurement.builder()
                .id(1)
                .humidity(45.8)
                .temperature(24.9)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static Measurement getSecondMeasurementPersisted(Sensor sensor) {
        return Measurement.builder()
                .id(2)
                .humidity(40.9)
                .temperature(16.4)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static Measurement getThirdMeasurementPersisted(Sensor sensor) {
        return Measurement.builder()
                .id(3)
                .humidity(55.2)
                .temperature(29.6)
                .sensor(sensor)
                .time(LocalDateTime.now().minusDays(10))
                .build();
    }

    public static MeasurementDTO getPanicMeasurementDTO(SensorDTO sensor) {
        return MeasurementDTO.builder()
                .humidity(45.8)
                .temperature(90.9)
                .sensor(sensor)
                .build();
    }

    public static Measurement getPanicMeasurement(Sensor sensor) {
        return Measurement.builder()
                .humidity(45.8)
                .temperature(90.9)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static Measurement getFirstMeasurementTransient(Sensor sensor) {
        return Measurement.builder()
                .humidity(45.8)
                .temperature(24.9)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static Measurement getSecondMeasurementTransient(Sensor sensor) {
        return Measurement.builder()
                .humidity(40.9)
                .temperature(16.4)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static Measurement getThirdMeasurementTransient(Sensor sensor) {
        return Measurement.builder()
                .humidity(55.2)
                .temperature(29.6)
                .sensor(sensor)
                .time(LocalDateTime.now().minusDays(10))
                .build();
    }

    public static Measurement getFirstCriticalMeasurementTransient(Sensor sensor) {
        return Measurement.builder()
                .humidity(55.2)
                .temperature(90.6)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }
    public static Measurement getSecondCriticalMeasurementTransient(Sensor sensor) {
        return Measurement.builder()
                .humidity(55.2)
                .temperature(3.0)
                .sensor(sensor)
                .time(LocalDateTime.now())
                .build();
    }

    public static MeasurementDTO getFirstMeasurementDTO(SensorDTO sensorDto) {
        return MeasurementDTO.builder()
                .humidity(45.8)
                .temperature(24.9)
                .sensor(sensorDto)
                .build();
    }

    public static SensorSetup getSensorSetup() {
        return SensorSetup.builder()
                .startAt(LocalTime.of(8,0))
                .endAt(LocalTime.of(16, 30))
                .minimalTemperature(15.0)
                .optimalTemperature(25.0)
                .serial(156000)
                .build();
    }

    public static SensorSetupDTO getSensorSetupDTO() {
        return SensorSetupDTO.builder()
                .startAt(480)
                .endAt(990)
                .minimalTemperature(15.0)
                .optimalTemperature(25.0)
                .serial(156000)
                .build();
    }

    public static List<Measurement> getMeasurementsTransient(Sensor sensor) {
        return List.of(getFirstMeasurementTransient(sensor), getSecondMeasurementTransient(sensor), getThirdMeasurementTransient(sensor));
    }

    public static List<Measurement> getMeasurementsPersisted(Sensor sensor) {
        return List.of(getFirstMeasurementPersisted(sensor), getSecondMeasurementPersisted(sensor), getThirdMeasurementPersisted(sensor));
    }

    public static List<MyUser> getUsersPersisted() {
        return List.of(getThirdUserPersisted(), getFirstUserPersisted(), getSecondUserPersisted());
    }
    public static List<MyUser> getUsersTransient() {
        return List.of(getThirdUserTransient(), getFirstUserTransient(), getSecondUserTransient());
    }

    public static List<Sensor> getSensorsTransient() {
        return List.of(getKostromaSensorTransient(), getYaroslavlSensorTransient(), getIvanovoSensorTransient());
    }

    public static List<Sensor> getSensorsPersisted() {
        return List.of(getKostromaSensorPersisted(), getYaroslavlSensorPersisted(), getIvanovoSensorPersisted());
    }

    public static List<SensorDTO> getSensorDTOs() {
        return List.of(getKostromaSensorDto(), getYaroslavlSensorDto(), getIvanovoSensorDto());
    }

    public static SensorDTO getKostromaSensorDto() {
        return SensorDTO.builder()
                .name("Kostroma")
                .serial(156000)
                .build();
    }

    public static SensorDTO getYaroslavlSensorDto() {
        return SensorDTO.builder()
                .name("Yaroslavl")
                .serial(150000)
                .build();
    }

    public static SensorDTO getIvanovoSensorDto() {
        return SensorDTO.builder()
                .name("Ivanovo")
                .serial(153000)
                .build();
    }
}