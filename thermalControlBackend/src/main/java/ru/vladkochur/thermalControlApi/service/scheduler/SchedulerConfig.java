package ru.vladkochur.thermalControlApi.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.vladkochur.thermalControlApi.service.serviceInterface.MeasurementService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;

import java.time.LocalDateTime;

@EnableScheduling
@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final SensorPeriodService sensorPeriodService;
    private final MeasurementService measurementService;

    @Scheduled(cron = "0 0 0,1,2 * * 1")
    public void everyMondayActualPeriodClearing() {
        sensorPeriodService.deleteAllActual();
        log.info(String.format("Actual periods are deleted %s", LocalDateTime.now()));
    }

        @Scheduled(cron = "0 0 0,1,2 1 4 *")
    public void everyFirstAprilOldMeasurementsClearing() {
        measurementService.deleteMeasurementsByTimeBefore(
                LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0));
        log.info(String.format("Old measurements are deleted %s", LocalDateTime.now()));
    }
}
