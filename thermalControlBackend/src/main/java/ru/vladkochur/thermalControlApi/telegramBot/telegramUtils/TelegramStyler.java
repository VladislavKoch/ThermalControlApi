package ru.vladkochur.thermalControlApi.telegramBot.telegramUtils;

import org.springframework.stereotype.Component;
import ru.vladkochur.thermalControlApi.entity.Measurement;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.entity.SensorSettings;

import java.time.format.DateTimeFormatter;

@Component
public class TelegramStyler {

    public static String sensorToTelegramStyleSingleRow(Sensor sensor) {
        StringBuilder sb = new StringBuilder()
                .append("serial : ").append(sensor.getSerial()).append(", имя : ");
        if (sensor.getName() != null) {
            sb.append(sensor.getName());
        } else {
            sb.append("не задано");
        }
        return sb.toString();
    }

    public static String sensorToTelegramStyle(Sensor sensor) {
        StringBuilder sb = new StringBuilder()
                .append("serial : ").append(sensor.getSerial()).append("\nИмя : ");
        if (sensor.getName() != null) {
            sb.append(sensor.getName());
        } else {
            sb.append("не задано");
        }
        return sb.toString();
    }

    public static String measurementToTelegramStyleSingleRow(Measurement measurement) {
        StringBuilder sb = new StringBuilder()
                .append('\n')
                .append(measurement.getTemperature())
                .append("℃,     ").append(measurement.getHumidity())
                .append("%,     ").append(measurement.getTime().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yy")));
        return sb.toString();
    }

    public static String measurementToTelegramStyle(Measurement measurement) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nВремя : ").append(measurement.getTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")))
                .append(",\nТемпература : ").append(measurement.getTemperature())
                .append("℃,\nВлажность : ").append(measurement.getHumidity()).append("%");
        return sb.toString();
    }

    public static String measurementToTelegramStyle(Measurement measurement, Sensor sensor) {
        StringBuilder sb = new StringBuilder();
        if (sensor.getName() != null) {
            sb.append("\nИмя датчика : ")
                    .append(sensor.getName());
        } else {
            sb.append("\nНомер датчика : ")
                    .append(sensor.getSerial());
        }
        sb.append("\nВремя : ").append(measurement.getTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")))
                .append(",\nТемпература : ").append(measurement.getTemperature())
                .append("℃,\nВлажность : ").append(measurement.getHumidity()).append("%");
        return sb.toString();
    }

    public static String settingsToTelegramStyle(SensorSettings settings) {
        Sensor sensor = settings.getSensor();
        StringBuilder sb = new StringBuilder()
                .append("\n\n");
        if (sensor == null) {
            sb.append("По умолчанию");
        } else {
            sb.append(sensor.getName() != null ? sensor.getName() : sensor.getSerial());
        }
        sb.append("\nОптимальная температура : ")
                .append(settings.getOptimalTemperature())
                .append("℃")
        .append("\nМинимальная температура : ")
                .append(settings.getMinimalTemperature())
                .append("℃");
        return sb.toString();
    }

    public static String periodToTelegramStyle(SensorPeriod period) {
        StringBuilder sb = new StringBuilder()
                .append("\n\n")
                .append(period.getIsDefault() ? "По умолчанию : " :
                        "------------------------------------------------------\nНа эту неделю : ")
                .append("\nДень недели : ")
                .append(period.getWeekday().getMessage())
                .append("\nНачало режима обогрева : ")
                .append(period.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .append("\nКонец режима обогрева : ")
                .append(period.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .append(period.getIsDefault() ? "" : "\n------------------------------------------------------");

        return sb.toString();
    }
}
