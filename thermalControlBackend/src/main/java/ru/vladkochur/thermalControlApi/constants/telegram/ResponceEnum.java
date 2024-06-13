package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum ResponceEnum {
    WANT_TO_LOG_IN("Вы не являетесь авторизированным пользователем!\n" +
            "Запросите авторизацию через пользовательское меню!"),
    AUTHORIZATION("Запрос на авторизацию отправлен!"),
    CRITICAL_TEMPERATURE_ERROR("Нарушена логика программы датчиков"),
    BAD_REQUEST("Команда не найдена"),
    BAD_FORMAT("Бот умеет обрабатывать только текстовые команды!"),
    BAD_SERIAL("Неправильно указан серийный номер"),
    BAD_COMMAND("Команда введена с ошибкой"),
    HOW_TO("Как указать?");

    private final String message;

    ResponceEnum(String message) {
        this.message = message;
    }

}
