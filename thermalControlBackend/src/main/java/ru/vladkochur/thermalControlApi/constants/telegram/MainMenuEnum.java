package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum MainMenuEnum {
    SENSORS("Датчики"),
    SENSOR_SETTINGS("Установка температуры"),
    SENSOR_PERIOD("Установка времени работы"),
    SENSOR_MEASUREMENTS("Измерения датчиков"),
    MY_STATUS("Мой статус"),
    ADMINISTRATION("Администрирование"),
    HELLO_USER("Приветствую %s! \nВыберите команду :"),
    USER_STATUS("Приветствую %s(%s)! \nВаш Telegram id : %s, \nВаши полномочия : %s");

    private final String message;

    MainMenuEnum(String message){
        this.message = message;
    }
}
