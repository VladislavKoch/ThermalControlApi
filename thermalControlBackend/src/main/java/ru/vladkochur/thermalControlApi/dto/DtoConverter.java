package ru.vladkochur.thermalControlApi.dto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vladkochur.thermalControlApi.entity.*;

import java.time.temporal.ChronoField;

@Component
public class DtoConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public DtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Measurement dtoToMeasurement(MeasurementDTO dto) {
        return modelMapper.map(dto, Measurement.class);
    }

    public MeasurementDTO measurementToDto(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }

    public Sensor dtoToSensor(SensorDTO dto) {
        return modelMapper.map(dto, Sensor.class);
    }

    public SensorDTO sensorToDto(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    public MyUser dtoToUser(MyUserDTO dto) {
        return modelMapper.map(dto, MyUser.class);
    }

    public MyUserDTO userToDto(MyUser user) {
        return modelMapper.map(user, MyUserDTO.class);
    }

    public TelegramInteraction dtoToTelegramInteraction(TelegramInteractionDTO dto) {
        return modelMapper.map(dto, TelegramInteraction.class);
    }

    public TelegramInteractionDTO telegramInteractionToDto(TelegramInteraction telegramInteraction) {
        return modelMapper.map(telegramInteraction, TelegramInteractionDTO.class);
    }

    public SensorSetup dtoToSensorSetup(SensorSetupDTO dto) {
        return modelMapper.map(dto, SensorSetup.class);
    }

    public SensorSetupDTO sensorSetupToDto(SensorSetup sensorSetup) {
        return SensorSetupDTO.builder()
                .serial(sensorSetup.getSerial())
                .startAt(sensorSetup.getStartAt().get(ChronoField.MINUTE_OF_DAY))
                .endAt(sensorSetup.getEndAt().get(ChronoField.MINUTE_OF_DAY))
                .minimalTemperature(sensorSetup.getMinimalTemperature())
                .optimalTemperature(sensorSetup.getOptimalTemperature())
                .build();
    }
}
