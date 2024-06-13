package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum SensorsEnum {
    SENSOR_GET_DATA("Информация о датчике"),
    SENSOR_DELETE("Удалить датчик"),
    SENSOR_UPDATE("Изменить имя датчика"),
    SENSOR_GET_INTERACTIONS("Датчики запросившие взаимодействие"),
    SENSOR_CLEAR_INTERACTIONS("Очистить список взаимодействия"),
    SENSOR_BACK("↩️"),
    SENSOR_EDIT_BACK("↩️"),
    SENSORS_BACK("↩️"),
    SENSOR_HELP_NAME_UPDATE(
            "Нажмите на пункт меню, напишите новое имя, разрешены любые символы, кроме нижнего подчеркивания. " +
                    "Имя должно содержать от 3 до 100 символов. Затем отправьте сообщение"),
    NAME_SUCCESS("Имя датчика успешно изменено"),
    BAD_NAME("Имя не соответствует правилам"),
    BAD_SENSOR("Датчика не существует"),
    BAD_SENSORS("Датчики не найдены");

    private final String message;

    SensorsEnum(String message){
        this.message = message;
    }
}
