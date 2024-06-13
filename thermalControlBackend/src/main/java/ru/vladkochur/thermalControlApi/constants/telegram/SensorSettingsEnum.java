package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum SensorSettingsEnum {
    SETTINGS_GET_ALL("Список всех настроек"),
    SETTINGS_GET_BY_SERIAL("Список настроек датчика"),
    SETTINGS_UPDATE_DEFAULT("Задать настройки по умолчанию"),
    SETTINGS_DELETE("Сброс настроек для датчика"),
    SETTINGS_UPDATE("Задать настройки"),
    SETTINGS_BACK("↩️"),
    SETTINGS_SENSORS_BACK("↩️"),
    SETTINGS_UPDATE_BACK("↩️"),
    SETTINGS_HELP_TEMPERATURE(
            "Нажмите на пункт меню, напишите сначала минимальную , потом оптимальную температуру через пробел " +
                    "целым или дробным числом (через точку), затем отправьте сообщение. Температуры должны находиться " +
                    "в диапазоне от 5 до 50 градусов, минимальная не должна быть больше максимальной"),
    SETTINGS_UP_DEF_("Температуры по умолчанию"),
    BAD_TEMPERATURE("Температура была введена некорректно"),
    BAD_TEMPERATURE_VALUES("Указанные температуры нарушают правила"),
    CRITICAL_TEMPERATURE_LOW("ОПАСТНОСТЬ РАЗМОРОЗКИ КОНТУРА"),
    CRITICAL_TEMPERATURE_HIGH("ОПАСТНОСТЬ ПОЖАРА"),
    SUCCESS_TEMPERATURE("Температура была успешно задана"),
    CLEAR_ACTUAL_SETTINGS("Настройки температуры для датчика сброшены");


    private final String message;

    SensorSettingsEnum(String message) {
        this.message = message;
    }

}
