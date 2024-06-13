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
import ru.vladkochur.thermalControlApi.dto.SensorDTO;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSetupService;
import ru.vladkochur.thermalControlApi.utils.ErrorsUtil;
import ru.vladkochur.thermalControlApi.utils.SensorValidator;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/sensors")
@RequiredArgsConstructor
@Slf4j
public class SensorRestController {
    private final SensorService sensorService;
    private final SensorValidator sensorValidator;
    private final DtoConverter converter;
    private final SensorSetupService sensorSetupService;

    @GetMapping()
    public ResponseEntity<?> getAllSensors() {
        List<SensorDTO> sensorsDto = sensorService.findAll().stream().map(converter::sensorToDto).toList();
        return ResponseEntity.ok(sensorsDto);
    }

    @GetMapping("/{serial}")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public ResponseEntity<?> getSensorSetupBySerial(@PathVariable("serial") Integer serial) {
        return ResponseEntity.ok(converter.sensorSetupToDto(sensorSetupService.getSetupBySerial(serial)));
    }

    @PostMapping("/registration")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public ResponseEntity<?> createSensor(@RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        Sensor sensor = converter.dtoToSensor(sensorDTO);
        sensorValidator.validate(sensor, bindingResult);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        Sensor savedSensor = sensorService.save(sensor);
        log.info(String.format("sensor registered %s", LocalDateTime.now()));
        return ResponseEntity.ok(converter.sensorToDto(savedSensor));
    }

    @DeleteMapping("/{serial}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSensorBySerial(@PathVariable("serial") Integer serial) {
        sensorService.deleteBySerial(serial);
        log.info(String.format("sensor deleted %s", LocalDateTime.now()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateSensor(@RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        Sensor sensor = converter.dtoToSensor(sensorDTO);
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        Sensor updatedSensor = sensorService.update(sensor);
        SensorDTO result = converter.sensorToDto(updatedSensor);
        log.info(String.format("sensor updated %s", LocalDateTime.now()));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/interaction")
    @PreAuthorize("hasAuthority('ROLE_SENSOR')")
    public ResponseEntity<?> makeSensorWantedToInteract(@RequestBody @Valid SensorDTO sensorDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ErrorsUtil.sendErrorsToClient(bindingResult);
        }
        Sensor sensor = converter.dtoToSensor(sensorDTO);
        sensorService.makeSensorWantedToInteract(sensor);
        log.info(String.format("sensor interaction created %s", LocalDateTime.now()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/interaction")
    public ResponseEntity<?> getAllSensorsThatWantInteract() {
        return ResponseEntity.ok(sensorService.findAllSensorsWantedForInteraction().stream()
                .map(converter::sensorToDto).toList());
    }

    @DeleteMapping("/interaction")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> setAllSensorsUnwantedToInteract() {
        sensorService.makeAllSensorsUnwantedToInteract();
        log.info(String.format("sensor interactions truncated %s", LocalDateTime.now()));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
