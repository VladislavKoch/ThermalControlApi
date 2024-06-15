package ru.vladkochur.thermalControlApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.MeasurementDTO;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.exception.MeasurementsNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.MeasurementService;
import ru.vladkochur.thermalControlApi.utils.ErrorsUtil;
import ru.vladkochur.thermalControlApi.utils.MeasurementValidator;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/measurements")
@RequiredArgsConstructor
@Slf4j
public class MeasurementRestController {
    private final MeasurementService measurementService;
    private final MeasurementValidator measurementValidator;
    private final DtoConverter converter;

    @GetMapping()
    public List<MeasurementDTO> getAllMeasurements() {
        return measurementService.findAll().stream().map(converter::measurementToDto).toList();
    }
    @GetMapping("/{serial}")
    public List<MeasurementDTO> getAllSensorMeasurements(@PathVariable("serial") Integer serial) {
        return measurementService.findAllMeasurementsBySensorSerial(serial).stream().
                map(converter::measurementToDto).toList();
    }

    @GetMapping("/avg")
    public MeasurementDTO getAvgMeasurementValues() {
        return converter.measurementToDto(measurementService.findAvgMeasurement()
                .orElseThrow(MeasurementsNotFoundException::new));
    }

    @GetMapping("/avg/{serial}")
    public MeasurementDTO getAvgSensorMeasurementsValues(@PathVariable("serial") Integer serial) {
        return converter.measurementToDto(measurementService
                .findAvgMeasurementBySensorSerial(serial).orElseThrow(MeasurementsNotFoundException::new));
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public MeasurementDTO create(@RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        Measurement measurement = converter.dtoToMeasurement(measurementDTO);
        measurementValidator.validate(measurement, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        measurement.setTime(LocalDateTime.now());
        Measurement createdMeasurement = measurementService.save(measurement);
        log.info(String.format("measurement adding %s", LocalDateTime.now()));
        return converter.measurementToDto(createdMeasurement);
    }

    @PostMapping("/panic")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public void notifyAdmin(@RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        Measurement measurement = converter.dtoToMeasurement(measurementDTO);
        measurementValidator.validate(measurement, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        measurementService.sendCriticalMeasurementToAllUsers(measurement);
        log.info(String.format("panic measurement received %s", LocalDateTime.now()));
    }

}
