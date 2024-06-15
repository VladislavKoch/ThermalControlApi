package ru.vladkochur.thermalControlApi.telegramBot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vladkochur.thermalControlApi.constants.TelegramMenuEnum;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.Sensor;
import ru.vladkochur.thermalControlApi.entity.SensorPeriod;
import ru.vladkochur.thermalControlApi.exception.SensorNotFoundException;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorPeriodService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.SensorSettingsService;
import ru.vladkochur.thermalControlApi.telegramBot.keyboard.InlineKeyboardMaker;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.vladkochur.thermalControlApi.constants.TelegramMenuEnum.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.ReplyKeyboardMaker.getMenuKeyboard;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {
    final InlineKeyboardMaker inlineKeyboardMaker;
    final SensorService sensorService;
    final SensorSettingsService sensorSettingsService;
    final SensorPeriodService sensorPeriodService;

    @Value("${telegram.bot.name}")
    private String botName;

    public BotApiMethod<?> answerMessage(SendMessage sendMessage, Message message, boolean isAdmin) {
        sendMessage.setText(BAD_COMMAND.getMessage());
        sendMessage.setReplyMarkup(getMenuKeyboard());
        String inputText = message.getText();
        TelegramMenuEnum command = BAD_COMMAND;
        String identifier = null;
        String payload = null;

        if (Objects.equals(inputText, "/menu") | Objects.equals(inputText, "/start") |
                Objects.equals(inputText, WANT_TO_LOG_IN.name())) {
            sendMessage.setReplyMarkup(inlineKeyboardMaker.getMenuInlineKeyboard(isAdmin));
            sendMessage.setText(String.format(HELLO_USER.getMessage(), message.getChat().getFirstName()));
            return sendMessage;
        }

        try {
            if (inputText.matches(String.format("@%s [A-Za-z0-9]+_[A-Za-z0-9]+_[A-Za-z0-9]+_.+\\Z", botName))) {
                inputText = inputText.substring(inputText.indexOf(botName) + botName.length() + 1);
                String[] commandParts = inputText.split("_");
                command = Enum.valueOf(TelegramMenuEnum.class, String.format("%s_%s", commandParts[0], commandParts[1]));
                identifier = commandParts[2];
                payload = commandParts[3];
            } else {
                return sendMessage;
            }
        } catch (IllegalArgumentException ex) {
            log.warn(ex.getMessage());
            return sendMessage;
        }

        switch (command) {
            case SENSOR_UPDATE -> {
                if (payload.length() < 3 || payload.length() > 100) {
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
                    return sendMessage;
                }
            }
            case SETTINGS_UP -> {
                return setTemperatures(sendMessage, identifier, payload);
            }

            case PERIOD_D -> {
                SensorPeriod period = getPeriod(sendMessage, identifier, payload);
                if (period != null) {
                    sensorPeriodService.setOptimalTemperaturePeriod(period, true);
                    sendMessage.setText(SUCCESS_TIME.getMessage());
                }
            }
            case PERIOD_A -> {
                SensorPeriod period = getPeriod(sendMessage, identifier, payload);
                if (period != null) {
                    sensorPeriodService.setOptimalTemperaturePeriod(period, false);
                    sendMessage.setText(SUCCESS_TIME.getMessage());
                }
            }
        }
        return sendMessage;
    }

    private SendMessage setTemperatures(SendMessage sendMessage, String identifier, String payload) {
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
        if(identifier.matches("\\d+")){
            try{
                sensorSettingsService.setSettingsBySerial(optimalTemperature, minimalTemperature,
                        Integer.parseInt(identifier));
                sendMessage.setText(SUCCESS_TEMPERATURE.getMessage());
            } catch (SensorNotFoundException | IllegalArgumentException ex) {
                sendMessage.setText(BAD_SENSOR.getMessage());
            }

        }else {
            try {
                if (TelegramMenuEnum.valueOf(identifier) == DEF) {
                    sensorSettingsService.setDefaultSettings(optimalTemperature, minimalTemperature);
                    sendMessage.setText(SUCCESS_TEMPERATURE.getMessage());
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return sendMessage;
    }

    private SensorPeriod getPeriod(SendMessage sendMessage, String identifier, String payload) {
        LocalTime startsAt;
        LocalTime endsAt;
        Weekday weekday;
        try {
            String[] times = payload.split(" ");
            if (times[0].matches("HH:mm")) {
                startsAt = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                startsAt = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("H:mm"));
            }
            if (times[1].matches("HH:mm")) {
                endsAt = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                endsAt = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("H:mm"));
            }
            weekday = Weekday.values()[Integer.parseInt(identifier)];
        } catch (DateTimeException e) {
            sendMessage.setText(BAD_TIME.getMessage());
            return null;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            sendMessage.setText(BAD_COMMAND.getMessage());
            return null;
        }
        return SensorPeriod.builder()
                .startAt(startsAt)
                .endAt(endsAt)
                .weekday(weekday)
                .build();
    }
}