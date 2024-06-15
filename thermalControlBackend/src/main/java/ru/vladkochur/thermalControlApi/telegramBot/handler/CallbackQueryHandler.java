package ru.vladkochur.thermalControlApi.telegramBot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.vladkochur.thermalControlApi.constants.TelegramMenuEnum;
import ru.vladkochur.thermalControlApi.entity.*;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.MeasurementService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSettingsService;
import ru.vladkochur.thermalControlApi.telegramBot.keyboard.InlineKeyboardMaker;
import ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.GraphUrlMaker;
import ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.vladkochur.thermalControlApi.constants.TelegramMenuEnum.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.InlineKeyboardMaker.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.ReplyKeyboardMaker.getMenuKeyboard;
import static ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler.measurementToTelegramStyle;
import static ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler.settingsToTelegramStyle;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler {
    final MyUserService userService;
    final SensorService sensorService;
    final MeasurementService measurementService;
    final GraphUrlMaker graphUrlMaker;
    final SensorSettingsService sensorSettingsService;
    final SensorPeriodService sensorPeriodService;
    final InlineKeyboardMaker inlineKeyboardMaker;


    public BotApiMethod<?> answerCallback(SendMessage sendMessage, CallbackQuery callbackQuery, boolean isAdmin) {
        EditMessageReplyMarkup editMessage = new EditMessageReplyMarkup();
        editMessage.setChatId(callbackQuery.getMessage().getChatId());
        editMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        String callData = callbackQuery.getData();

        List<Measurement> measurements = null;
        List<Sensor> sensors = null;
        Sensor sensor = null;
        Integer serial = null;

        sendMessage.setText(BAD_SENSORS.getMessage());
        sendMessage.setReplyMarkup(getMenuKeyboard());
        TelegramMenuEnum command = BAD_COMMAND;

        try {
            if (callData.matches("\\w+_\\d+\\Z")) {
                serial = Integer.parseInt(callData.substring(callData.lastIndexOf("_") + 1));
                sensor = sensorService.findBySerial(serial).orElseThrow(SensorNotFoundException::new);
                callData = callData.substring(0, callData.lastIndexOf("_"));
            }
            command = Enum.valueOf(TelegramMenuEnum.class, callData);
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            sendMessage.setText(BAD_COMMAND.getMessage());
            return sendMessage;
        } catch (SensorNotFoundException ex){
            log.warn(ex.getMessage());
            sendMessage.setText(BAD_SENSOR.getMessage());
            return sendMessage;
        }

        switch (command) {
            case SENSORS, SENSOR_BACK -> {
                editMessage.setReplyMarkup(getSensorsListInlineKeyboard(sensorService.findAll()));
            }

            case SENSOR_MEASUREMENTS, MEASUREMENTS_SERIAL_BACK -> {
                editMessage.setReplyMarkup(getMeasurementsInlineKeyboard());
            }

            case SENSORS_BACK, MEASUREMENTS_BACK, SETTINGS_BACK, PERIOD_BACK -> {
                editMessage.setReplyMarkup(inlineKeyboardMaker.getMenuInlineKeyboard(isAdmin));
            }

            case SENSOR_GET_INTERACTIONS -> {
                editMessage.setReplyMarkup(getSensorsInteractionInlineKeyboard(isAdmin,
                        sensorService.findAllSensorsWantedForInteraction()));
            }

            case SENSOR_CLEAR_INTERACTIONS -> {
                if (isAdmin) {
                    sensorService.makeAllSensorsUnwantedToInteract();
                }
                sendMessage.setText("Список очищен");
                return sendMessage;
            }

            case MEASUREMENTS_GET_ALL -> {
                measurements = measurementService.findLastWeekMeasurements();
                if (!measurements.isEmpty()) {
                    sendMessage.setText(cutListBrackets(measurementService.findLastWeekMeasurements().stream()
                            .map(TelegramStyler::measurementToTelegramStyleSingleRow).toList()));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case MEASUREMENTS_GET_BY_SERIAL -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForMeasurementsInlineKeyboard(sensors));
                } else {
                    measurements = measurementService.findLastWeekMeasurementsBySensorSerial(serial);
                    if (!measurements.isEmpty()) {
                        String measurementsString = cutListBrackets(measurements.stream().map(
                                TelegramStyler::measurementToTelegramStyleSingleRow).toList());
                        sendMessage.setText(String.format("%s\n%s", getName(sensor), measurementsString));
                    } else {
                        sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                    }
                    return sendMessage;
                }
            }

            case MEASUREMENTS_GET_AVG -> {
                measurements = measurementService.findLastWeekMeasurements();
                if (!measurements.isEmpty()) {
                    sendMessage.setText("\nСредние показатели : " +
                            measurementToTelegramStyle(measurementService.findAvgMeasurement().get()));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case MEASUREMENTS_GET_AVG_BY_SERIAL -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForMeasurementsAvgInlineKeyboard(sensors));
                } else {
                    Optional<Measurement> measurement = measurementService.findAvgMeasurementBySensorSerial(serial);
                    if (measurement.isPresent()) {
                        sendMessage.setText(TelegramStyler
                                .measurementToTelegramStyle(measurement.get(), sensor));
                    }else {
                        sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                    }
                    return sendMessage;
                }
            }

            case MEASUREMENTS_GET_GRAPH -> {
                measurements = measurementService.findLastWeekMeasurements();
                if (!measurements.isEmpty()) {
                    sendMessage.setText(graphUrlMaker.makeMeasuresGraph(measurements));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case MEASUREMENTS_GET_GRAPH_BY_SERIAL -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForGraphAvgInlineKeyboard(sensors));
                } else {
                    measurements = measurementService.findLastWeekMeasurementsBySensorSerial(serial);
                    if (!measurements.isEmpty()) {
                        sendMessage.setText(String.format("%s\n%s",
                                getName(sensor), graphUrlMaker.makeMeasuresGraph(measurements)));
                    } else {
                        sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                    }
                    return sendMessage;
                }
            }

            case MEASUREMENTS_GET_DAILY_BY_SERIAL -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForDailyGraphInlineKeyboard(sensors));
                } else {
                    measurements = measurementService.findDailyMeasurementsBySensorSerial(serial);
                    if (!measurements.isEmpty()) {
                        sendMessage.setText(String.format("%s\n%s",
                                getName(sensor), graphUrlMaker.makeMeasuresGraph(measurements)));
                    }else {
                        sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                    }
                    return sendMessage;
                }
            }

            case SENSOR_SETTINGS, SETTINGS_SENSORS_BACK -> {
                editMessage.setReplyMarkup(getSettingsInlineKeyboard());
            }

            case SETTINGS_GET_ALL -> {
                List<SensorSettings> settings = enrichSettings(sensorSettingsService.getAllSettings());
                sendMessage.setText(
                        cutListBrackets(settings.stream().map(TelegramStyler::settingsToTelegramStyle).toList()));
                return sendMessage;
            }

            case SETTINGS_GET_BY_SERIAL -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForSettingsInlineKeyboard(sensors));
                } else {
                    Optional<SensorSettings> settings = sensorSettingsService.getSettingsBySerial(serial);
                    if (settings.isPresent()) {
                        sendMessage.setText(settingsToTelegramStyle(settings.get()));
                    } else {
                        sendMessage.setText("Используются настройки : " +
                                settingsToTelegramStyle(sensorSettingsService.getDefaultSettings().get()));
                    }
                }
            }

            case SETTINGS_UPDATE -> {
                sensors = sensorService.findAll();
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSettingsUpdateInlineKeyboard(isAdmin, sensors));
            }

            case SETTINGS_DELETE -> {
                if (sensor == null) {
                    sensors = sensorService.findAll();
                    if (sensors.isEmpty()) {
                        return sendMessage;
                    }
                    editMessage.setReplyMarkup(getSensorsListForSettingsDeleteInlineKeyboard(isAdmin, sensors));
                } else {
                    if (isAdmin) {
                        sensorSettingsService.deleteSensorSettings(serial);
                    }
                    sendMessage.setText(CLEAR_ACTUAL_SETTINGS.getMessage());
                }
            }

            case SENSOR_PERIOD, PERIOD_BACK_FROM -> {
                editMessage.setReplyMarkup(getPeriodInlineKeyboard(isAdmin));
            }

            case PERIOD_GET_ALL -> {
                sendMessage.setText(cutListBrackets(sensorPeriodService.getAll().stream()
                        .map(TelegramStyler::periodToTelegramStyle).toList()));
                return sendMessage;
            }

            case PERIOD_GET_ACTUAL -> {
                List<SensorPeriod> periods = sensorPeriodService.getActual();
                if (periods.isEmpty()) {
                    sendMessage.setText(EMPTY_PERIODS.getMessage());
                } else {
                    sendMessage.setText(cutListBrackets(periods.stream()
                            .map(TelegramStyler::periodToTelegramStyle).toList()));
                }
                return sendMessage;
            }

            case PERIOD_DELETE_ACTUAL -> {
                if (isAdmin) {
                    sensorPeriodService.deleteAllActual();
                }
                sendMessage.setText(CLEAR_ACTUAL_PERIODS.getMessage());
                return sendMessage;
            }

            case PERIOD_SET_DEFAULT -> {
                editMessage.setReplyMarkup(getWeekdaySetDefaultInlineKeyboard());
            }

            case PERIOD_SET_ACTUAL -> {
                editMessage.setReplyMarkup(getWeekdaySetActualInlineKeyboard());
            }

            case SETTINGS_HELP_TEMPERATURE -> {
                sendMessage.setText(SETTINGS_HELP_TEMPERATURE.getMessage());
                return sendMessage;
            }

            case PERIOD_HELP_TIME -> {
                sendMessage.setText(PERIOD_HELP_TIME.getMessage());
                return sendMessage;
            }

            case SENSOR_HELP_NAME_UPDATE -> {
                sendMessage.setText(SENSOR_HELP_NAME_UPDATE.getMessage());
                return sendMessage;
            }

            case MY_STATUS -> {
                MyUser user =
                        userService.findUserByTelegramId(callbackQuery.getMessage().getChat().getId().toString()).get();
                String roles = cutListBrackets(Arrays.stream(user.getRoles().split(", ")).map(x -> x.substring(
                        x.indexOf("ROLE_") + 5)).toList());
                sendMessage.setText(
                        String.format(USER_STATUS.getMessage(), callbackQuery.getMessage().getChat().getUserName(),
                                user.getLogin(), user.getTelegram(), roles));
                return sendMessage;
            }

            case SENSOR_MENU -> {
                editMessage.setReplyMarkup(getSensorInlineKeyboard(isAdmin, serial.toString()));
            }

            case SENSOR_GET_DATA -> {
                sendMessage.setText(TelegramStyler.sensorToTelegramStyle(sensor));
                return sendMessage;
            }

            case SENSOR_DELETE -> {
                if (isAdmin) {
                    sensorService.deleteBySerial(serial);
                }
                editMessage.setReplyMarkup(getSensorsListInlineKeyboard(sensorService.findAll()));
            }

            case BAD_COMMAND -> {
                sendMessage.setText(BAD_COMMAND.getMessage());
                return sendMessage;
            }
        }
        return editMessage;
    }

    private List<SensorSettings> enrichSettings(List<SensorSettings> settings) {
        return settings.stream().peek(x -> {
            Sensor sensor = x.getSensor();
            x.setSensor(sensor != null ? sensorService.findBySerial(sensor.getSerial()).get() : null);
        }).toList();
    }

    private String cutListBrackets(List<?> list) {
        String listString = list.toString();
        return listString.substring(1, listString.length() - 1);
    }

    private String getName(Sensor sensor) {
        return sensor.getName() != null ? sensor.getName() : sensor.getSerial().toString();
    }
}
