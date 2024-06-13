package ru.vladkochur.thermalControlApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAllMeasurements() {
        List<MeasurementDTO> measurements = measurementService.findAll().stream()
                .map(converter::measurementToDto).toList();
        return ResponseEntity.ok(measurements);
    }
    @GetMapping("/{serial}")
    public ResponseEntity<?> getAllSensorMeasurements(@PathVariable("serial") Integer serial) {
        List<MeasurementDTO> measurementDTOs = measurementService.findAllMeasurementsBySensorSerial(serial).stream().
                map(converter::measurementToDto).toList();
        return ResponseEntity.ok(measurementDTOs);
    }

    @GetMapping("/avg")
    public ResponseEntity<?> getAvgMeasurementValues() {
        MeasurementDTO measurementDTO = converter.measurementToDto(
                measurementService.findAvgMeasurement().orElseThrow(MeasurementsNotFoundException::new));
        return ResponseEntity.ok(measurementDTO);
    }

    @GetMapping("/avg/{serial}")
    public ResponseEntity<?> getAvgSensorMeasurementsValues(@PathVariable("serial") Integer serial) {
        MeasurementDTO measurementDTO = converter.measurementToDto(measurementService
                .findAvgMeasurementBySensorSerial(serial).orElseThrow(MeasurementsNotFoundException::new));
        return ResponseEntity.ok(measurementDTO);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public ResponseEntity<?> create(@RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        Measurement measurement = converter.dtoToMeasurement(measurementDTO);
        measurementValidator.validate(measurement, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        measurement.setTime(LocalDateTime.now());
        Measurement createdMeasurement = measurementService.save(measurement);
        log.info(String.format("measurement adding %s", LocalDateTime.now()));
        return ResponseEntity.ok(converter.measurementToDto(createdMeasurement));
    }

    @PostMapping("/panic")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public ResponseEntity<?> notifyAdmin(@RequestBody @Valid MeasurementDTO measurementDTO, BindingResult bindingResult) {
        Measurement measurement = converter.dtoToMeasurement(measurementDTO);
        measurementValidator.validate(measurement, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        measurementService.sendCriticalMeasurementToAllUsers(measurement);
        log.info(String.format("panic measurement received %s", LocalDateTime.now()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
