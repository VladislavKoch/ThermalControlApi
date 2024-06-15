package ru.vladkochur.thermalControlApi.errorHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.vladkochur.thermalControlApi.dto.ErrorResponseDTO;
import ru.vladkochur.thermalControlApi.exception.DataIsNotCorrectException;
import ru.vladkochur.thermalControlApi.exception.MeasurementsNotFoundException;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({SensorNotFoundException.class, MeasurementsNotFoundException.class,
            DataIsNotCorrectException.class})
    protected ResponseEntity<?> handleRestControllersExceptions(ResponseStatusException ex) {
        log.warn(ex.getReason());
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getReason()), ex.getStatusCode());
    }

    @ExceptionHandler({UsernameNotFoundException.class, MyUserNotFoundException.class})
    protected String handleMyUserControllerExceptions(Exception ex) {
        log.warn(ex.getMessage());
        return "redirect:/api/v1/administration/users";
    }
}
