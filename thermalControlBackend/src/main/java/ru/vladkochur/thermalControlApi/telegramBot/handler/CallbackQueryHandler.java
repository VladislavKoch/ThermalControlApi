package ru.vladkochur.thermalControlApi.telegramBot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.vladkochur.thermalControlApi.entity.*;
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

import static ru.vladkochur.thermalControlApi.constants.telegram.MainMenuEnum.USER_STATUS;
import static ru.vladkochur.thermalControlApi.constants.telegram.MeasurementsEnum.BAD_MEASUREMENTS;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorSettingsEnum.CLEAR_ACTUAL_SETTINGS;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorSettingsEnum.SETTINGS_HELP_TEMPERATURE;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsEnum.BAD_SENSORS;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsEnum.SENSOR_HELP_NAME_UPDATE;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsPeriodEnum.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.InlineKeyboardMaker.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.ReplyKeyboardMaker.getMenuKeyboard;
import static ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler.measurementToTelegramStyle;
import static ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler.settingsToTelegramStyle;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
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

        List<Sensor> sensors = sensorService.findAll();
        List<Measurement> measurements = measurementService.findLastWeekMeasurements();
        sendMessage.setText(BAD_SENSORS.getMessage());
        sendMessage.setReplyMarkup(getMenuKeyboard());

        switch (callData) {
            case "SENSORS", "SENSOR_BACK" -> {
                editMessage.setReplyMarkup(getSensorsListInlineKeyboard(sensorService.findAll()));
            }

            case "SENSOR_MEASUREMENTS", "MEASUREMENTS_SERIAL_BACK" -> {
                editMessage.setReplyMarkup(getMeasurementsInlineKeyboard());
            }

            case "SENSORS_BACK", "MEASUREMENTS_BACK", "SETTINGS_BACK", "PERIOD_BACK" -> {
                editMessage.setReplyMarkup(inlineKeyboardMaker.getMenuInlineKeyboard(isAdmin));
            }

            case "SENSOR_GET_INTERACTIONS" -> {
                editMessage.setReplyMarkup(getSensorsInteractionInlineKeyboard(isAdmin,
                        sensorService.findAllSensorsWantedForInteraction()));
            }

            case "SENSOR_CLEAR_INTERACTIONS" -> {
                if (isAdmin) {
                    sensorService.makeAllSensorsUnwantedToInteract();
                }
                sendMessage.setText("Список очищен");
                return sendMessage;
            }

            case "MEASUREMENTS_GET_ALL" -> {
                if (!measurements.isEmpty()) {
                    sendMessage.setText(cutListBrackets(measurementService.findLastWeekMeasurements().stream()
                            .map(TelegramStyler::measurementToTelegramStyleSingleRow).toList()));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case "MEASUREMENTS_GET_BY_SERIAL" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForMeasurementsInlineKeyboard(sensors));
            }

            case "MEASUREMENTS_GET_AVG" -> {
                if (!measurements.isEmpty()) {
                    sendMessage.setText("\nСредние показатели : " +
                            measurementToTelegramStyle(measurementService.findAvgMeasurement().get()));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case "MEASUREMENTS_GET_AVG_BY_SERIAL" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForMeasurementsAvgInlineKeyboard(sensors));
            }

            case "MEASUREMENTS_GET_GRAPH" -> {
                if (!measurements.isEmpty()) {
                    sendMessage.setText(graphUrlMaker.makeMeasuresGraph(measurements));
                } else {
                    sendMessage.setText(BAD_MEASUREMENTS.getMessage());
                }
                return sendMessage;
            }

            case "MEASUREMENTS_GET_GRAPH_BY_SERIAL" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForGraphAvgInlineKeyboard(sensors));
            }

            case "MEASUREMENTS_GET_DAILY_BY_SERIAL" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForDailyGraphInlineKeyboard(sensors));
            }

            case "SENSOR_SETTINGS", "SETTINGS_SENSORS_BACK" -> {
                editMessage.setReplyMarkup(getSettingsInlineKeyboard());
            }

            case "SETTINGS_GET_ALL" -> {
                List<SensorSettings> settings = enrichSettings(sensorSettingsService.getAllSettings());
                sendMessage.setText(
                        cutListBrackets(settings.stream().map(TelegramStyler::settingsToTelegramStyle).toList()));
                return sendMessage;
            }

            case "SETTINGS_GET_BY_SERIAL" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForSettingsInlineKeyboard(sensors));
            }

            case "SETTINGS_UPDATE" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSettingsUpdateInlineKeyboard(isAdmin, sensors));
            }

            case "SETTINGS_DELETE" -> {
                if (sensors.isEmpty()) {
                    return sendMessage;
                }
                editMessage.setReplyMarkup(getSensorsListForSettingsDeleteInlineKeyboard(isAdmin, sensors));
            }

            case "SENSOR_PERIOD", "PERIOD_BACK_FROM" -> {
                editMessage.setReplyMarkup(getPeriodInlineKeyboard(isAdmin));
            }

            case "PERIOD_GET_ALL" -> {
                sendMessage.setText(cutListBrackets(sensorPeriodService.getAll().stream()
                        .map(TelegramStyler::periodToTelegramStyle).toList()));
                return sendMessage;
            }

            case "PERIOD_GET_ACTUAL" -> {
                List<SensorPeriod> periods = sensorPeriodService.getActual();
                if (periods.isEmpty()) {
                    sendMessage.setText(EMPTY_PERIODS.getMessage());
                } else {
                    sendMessage.setText(cutListBrackets(periods.stream()
                            .map(TelegramStyler::periodToTelegramStyle).toList()));
                }
                return sendMessage;
            }

            case "PERIOD_DELETE_ACTUAL" -> {
                if (isAdmin) {
                    sensorPeriodService.deleteAllActual();
                }
                sendMessage.setText(CLEAR_ACTUAL_PERIODS.getMessage());
                return sendMessage;
            }

            case "PERIOD_SET_DEFAULT" -> {
                editMessage.setReplyMarkup(getWeekdaySetDefaultInlineKeyboard());
            }

            case "PERIOD_SET_ACTUAL" -> {
                editMessage.setReplyMarkup(getWeekdaySetActualInlineKeyboard());
            }

            case "SETTINGS_HELP_TEMPERATURE" -> {
                sendMessage.setText(SETTINGS_HELP_TEMPERATURE.getMessage());
                return sendMessage;
            }

            case "PERIOD_HELP_TIME" -> {
                sendMessage.setText(PERIOD_HELP_TIME.getMessage());
                return sendMessage;
            }

            case "SENSOR_HELP_NAME_UPDATE" -> {
                sendMessage.setText(SENSOR_HELP_NAME_UPDATE.getMessage());
                return sendMessage;
            }

            case "MY_STATUS" -> {
                MyUser user =
                        userService.findUserByTelegramId(callbackQuery.getMessage().getChat().getId().toString()).get();
                String roles = cutListBrackets(Arrays.stream(user.getRoles().split(", ")).map(x -> x.substring(
                        x.indexOf("ROLE_") + 5)).toList());
                sendMessage.setText(
                        String.format(USER_STATUS.getMessage(), callbackQuery.getMessage().getChat().getUserName(),
                                user.getLogin(), user.getTelegram(), roles));
                return sendMessage;
            }

            default -> {
                System.out.println(callData);
                String startsWith = callData.substring(0, callData.indexOf("_"));
                String command = callData.substring(callData.indexOf("_") + 1, callData.lastIndexOf("_"));
                Integer serial = Integer.parseInt(callData.substring(callData.lastIndexOf("_") + 1));
                Sensor sensor = sensorService.findBySerial(serial).get();

                switch (startsWith) {
                    case "SENSOR" -> {
                        switch (command) {
                            case "MENU" -> {
                                editMessage.setReplyMarkup(getSensorInlineKeyboard(isAdmin, serial.toString()));
                                return editMessage;
                            }
                            case "GET_DATA" -> {
                                sendMessage.setText(TelegramStyler.sensorToTelegramStyle(sensor));
                            }
                            case "DELETE" -> {
                                if (isAdmin) {
                                    sensorService.deleteBySerial(serial);
                                }
                                editMessage.setReplyMarkup(getSensorsListInlineKeyboard(sensorService.findAll()));
                                return editMessage;
                            }
                        }
                    }

                    case "MEASUREMENTS" -> {
                        String name = sensor.getName() != null ? sensor.getName() : sensor.getSerial().toString();
                        List<Measurement> sensorMeasurements = measurementService
                                .findLastWeekMeasurementsBySensorSerial(serial);

                        sendMessage.setText(BAD_MEASUREMENTS.getMessage());

                        switch (command) {
                            case "GET_BY_SERIAL" -> {
                                if (!sensorMeasurements.isEmpty()) {
                                    String measurementsString = cutListBrackets(sensorMeasurements.stream().map(
                                            TelegramStyler::measurementToTelegramStyleSingleRow).toList());
                                    sendMessage.setText(String.format("%s\n%s", name, measurementsString));
                                }
                            }
                            case "GET_AVG_BY_SERIAL" -> {
                                Optional<Measurement> measurement = measurementService.findAvgMeasurementBySensorSerial(serial);
                                if (measurement.isPresent()) {
                                    sendMessage.setText(TelegramStyler
                                            .measurementToTelegramStyle(measurement.get(), sensor));
                                }
                            }
                            case "GET_GRAPH_BY_SERIAL" -> {
                                if (!sensorMeasurements.isEmpty()) {
                                    sendMessage.setText(String.format("%s\n%s",
                                            name, graphUrlMaker.makeMeasuresGraph(sensorMeasurements)));
                                }
                            }
                            case "GET_DAILY_BY_SERIAL" -> {
                                List<Measurement> sensorDailyMeasurements =
                                        measurementService.findDailyMeasurementsBySensorSerial(serial);
                                if (!sensorDailyMeasurements.isEmpty()) {
                                    sendMessage.setText(String.format("%s\n%s",
                                            name, graphUrlMaker.makeMeasuresGraph(sensorDailyMeasurements)));
                                }
                            }
                        }
                    }

                    case "SETTINGS" -> {
                        switch (command) {
                            case "GET_BY_SERIAL" -> {
                                Optional<SensorSettings> settings = sensorSettingsService.getSettingsBySerial(serial);
                                if (settings.isPresent()) {
                                    sendMessage.setText(settingsToTelegramStyle(settings.get()));
                                } else {
                                    sendMessage.setText("Используются настройки : " +
                                            settingsToTelegramStyle(sensorSettingsService.getDefaultSettings().get()));
                                }
                            }
                            case "DELETE" -> {
                                if (isAdmin) {
                                    sensorSettingsService.deleteSensorSettings(serial);
                                }
                                sendMessage.setText(CLEAR_ACTUAL_SETTINGS.getMessage());
                            }
                        }
                    }
                }
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

}
