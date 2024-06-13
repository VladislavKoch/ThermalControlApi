package ru.vladkochur.thermalControlApi.telegramBot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSettingsService;
import ru.vladkochur.thermalControlApi.telegramBot.keyboard.InlineKeyboardMaker;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.vladkochur.thermalControlApi.constants.telegram.MainMenuEnum.HELLO_USER;
import static ru.vladkochur.thermalControlApi.constants.telegram.ResponceEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorSettingsEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsPeriodEnum.BAD_TIME;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsPeriodEnum.SUCCESS_TIME;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.ReplyKeyboardMaker.getMenuKeyboard;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    final InlineKeyboardMaker inlineKeyboardMaker;
    final SensorService sensorService;
    final SensorSettingsService sensorSettingsService;
    final SensorPeriodService sensorPeriodService;

    @Value("${telegram.bot.name}")
    private String botName;

    public BotApiMethod<?> answerMessage(SendMessage sendMessage, Message message, boolean isAdmin) {
        sendMessage.setText(BAD_COMMAND.getMessage());
        String inputText = message.getText();
        if (Objects.equals(inputText, WANT_TO_LOG_IN.name()) || Objects.equals(inputText, "/start")) {
            sendMessage.setReplyMarkup(getMenuKeyboard());
            sendMessage.setText(String.format(HELLO_USER.getMessage(), message.getChat().getFirstName()));

        } else if (Objects.equals(inputText, "/menu")) {
            sendMessage.setReplyMarkup(inlineKeyboardMaker.getMenuInlineKeyboard(isAdmin));
            sendMessage.setText(String.format(HELLO_USER.getMessage(), message.getChat().getFirstName()));
        } else if (isAdmin && inputText.matches("@.+_bot .+_.+_.+_.+")) {
            inputText = inputText.substring(inputText.indexOf(botName) + botName.length() + 1);

            String[] commandParts = inputText.split("_");

            String startsWith = commandParts[0];
            String command = commandParts[1];
            String identifier = commandParts[2];
            String payload = commandParts[3];

            switch (startsWith) {
                case "SENSOR" -> {
                    switch (command) {
                        case "UPDATE" -> {
                            if (payload.length() <= 3 || payload.length() >= 100) {
                                sendMessage.setText(BAD_NAME.getMessage());
                                return sendMessage;
                            }
                            try {
                                int serial = Integer.parseInt(identifier);
                                Sensor obtainedSensor = sensorService.findBySerial(serial)
                                        .orElseThrow(SensorNotFoundException::new);
                                obtainedSensor.setName(payload);
                                sensorService.update(obtainedSensor);
                                sendMessage.setText(NAME_SUCCESS.getMessage());
                            } catch (NumberFormatException | SensorNotFoundException e) {
                                sendMessage.setText(BAD_SERIAL.getMessage());
                            }
                        }
                    }
                }
                case "SETTINGS" -> {
                    return setTemperatures(sendMessage, command, payload, identifier);
                }
                case "PERIOD" -> {
                    return setPeriod(sendMessage, command, payload, identifier);
                }
            }
        }
        return sendMessage;
    }

    private SendMessage setTemperatures(SendMessage sendMessage, String command, String payload, String identifier) {
        if (command.equals("UP")) {
            double optimalTemperature;
            double minimalTemperature;
            try {
                String[] temperatures = payload.split(" ");
                optimalTemperature = Math.floor(Double.parseDouble(temperatures[1]) * 10) / 10;
                minimalTemperature = Math.floor(Double.parseDouble(temperatures[0]) * 10) / 10;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                sendMessage.setText(BAD_TEMPERATURE.getMessage());
                return sendMessage;
            }
            if (optimalTemperature < minimalTemperature || optimalTemperature < 5 || optimalTemperature > 50
                    || minimalTemperature < 5 || minimalTemperature > 50) {
                sendMessage.setText(BAD_TEMPERATURE_VALUES.getMessage());
                return sendMessage;
            }

            if (identifier.equals("DEF")) {
                sensorSettingsService.setDefaultSettings(optimalTemperature, minimalTemperature);
            } else {
                try {
                    int serial = Integer.parseInt(identifier);
                    sensorSettingsService.setSettingsBySerial(optimalTemperature, minimalTemperature, serial);
                } catch (NumberFormatException | SensorNotFoundException e) {
                    sendMessage.setText(BAD_SENSOR.getMessage());
                    return sendMessage;
                }
            }
            sendMessage.setText(SUCCESS_TEMPERATURE.getMessage());
        } else {
            sendMessage.setText(BAD_COMMAND.getMessage());
        }
        return sendMessage;
    }

    private SendMessage setPeriod(SendMessage sendMessage, String command, String payload, String identifier) {
        LocalTime startsAt;
        LocalTime endsAt;
        Weekday weekday;
        try {
            String[] times = payload.split(" ");
            if(times[0].matches("HH:mm")) {
                startsAt = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                startsAt = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("H:mm"));
            }
            if(times[1].matches("HH:mm")) {
                endsAt = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                endsAt = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("H:mm"));
            }
            weekday = Weekday.values()[Integer.parseInt(identifier)];
        } catch (DateTimeException e) {
            sendMessage.setText(BAD_TIME.getMessage());
            return sendMessage;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            sendMessage.setText(BAD_COMMAND.getMessage());
            return sendMessage;
        }

        switch (command) {
            case "D" -> {
                sensorPeriodService.setOptimalTemperaturePeriod(startsAt, endsAt, weekday, true);
                sendMessage.setText(SUCCESS_TIME.getMessage());
            }
            case "A" -> {
                sensorPeriodService.setOptimalTemperaturePeriod(startsAt, endsAt, weekday, false);
                sendMessage.setText(SUCCESS_TIME.getMessage());
            }
            default -> {
                sendMessage.setText(BAD_COMMAND.getMessage());
            }
        }
        return sendMessage;
    }
}