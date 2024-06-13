package ru.vladkochur.thermalControlApi.errorHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.vladkochur.thermalControlApi.dto.ErrorResponseDTO;
import ru.vladkochur.thermalControlApi.exception.DataIsNotCorrectException;
import ru.vladkochur.thermalControlApi.exception.MeasurementsNotFoundException;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SensorNotFoundException.class)
    protected ResponseEntity<?> handleSensorNotFoundException(SensorNotFoundException ex) {
        ErrorResponseDTO response = new ErrorResponseDTO("Sensor is not exist!");
        log.warn(response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MeasurementsNotFoundException.class)
    protected ResponseEntity<?> handleMeasurementsNotFoundException(MeasurementsNotFoundException ex) {
        ErrorResponseDTO response = new ErrorResponseDTO(ex.getMessage());
        log.warn(response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIsNotCorrectException.class)
    protected ResponseEntity<?> handleDataIsNotCorrectException(DataIsNotCorrectException ex) {
        ErrorResponseDTO response = new ErrorResponseDTO(ex.getMessage());
        log.warn(response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected String handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.info(ex.getMessage());
        return "redirect:/api/v1/administration/users";
    }

    @ExceptionHandler(MyUserNotFoundException.class)
    protected String handleUserIsNotFoundException(MyUserNotFoundException ex) {
        log.info(ex.getMessage());
        return "redirect:/api/v1/administration/users";
    }

}
