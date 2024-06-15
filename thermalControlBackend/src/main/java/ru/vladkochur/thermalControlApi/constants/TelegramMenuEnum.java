package ru.vladkochur.thermalControlApi.constants;

import lombok.Getter;

@Getter
public enum TelegramMenuEnum {
    SENSORS("Датчики"),
    SENSOR_SETTINGS("Установка температуры"),
    SENSOR_PERIOD("Установка времени работы"),
    SENSOR_MEASUREMENTS("Измерения датчиков"),
    MY_STATUS("Мой статус"),
    ADMINISTRATION("Администрирование"),
    HELLO_USER("Приветствую %s! \nВыберите команду :"),
    USER_STATUS("Приветствую %s(%s)! \nВаш Telegram id : %s, \nВаши полномочия : %s"),
    MEASUREMENTS_GET_ALL("Список измерений за неделю"),
    MEASUREMENTS_GET_BY_SERIAL("Список измерений датчика за неделю"),
    MEASUREMENTS_GET_AVG("Средние значения за неделю"),
    MEASUREMENTS_GET_AVG_BY_SERIAL("Средние значения датчика за неделю"),
    MEASUREMENTS_GET_GRAPH("График измерений за неделю"),
    MEASUREMENTS_GET_GRAPH_BY_SERIAL("График измерений датчика за неделю"),
    MEASUREMENTS_GET_DAILY_BY_SERIAL("График измерений датчика за день"),
    MEASUREMENTS_BACK("↩️"),
    MEASUREMENTS_SERIAL_BACK("↩️"),
    BAD_MEASUREMENTS("Измерения не найдены"),
    WANT_TO_LOG_IN("Вы не являетесь авторизированным пользователем!\n" +
            "Запросите авторизацию через пользовательское меню!"),
    AUTHORIZATION("Запрос на авторизацию отправлен!"),
    CRITICAL_TEMPERATURE_ERROR("Нарушена логика программы датчиков"),
    BAD_REQUEST("Команда не найдена"),
    BAD_FORMAT("Бот умеет обрабатывать только текстовые команды!"),
    BAD_SERIAL("Неправильно указан серийный номер"),
    BAD_COMMAND("Команда введена с ошибкой"),
    HOW_TO("Как указать?"),
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
    BAD_SENSORS("Датчики не найдены"),
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
    SETTINGS_UP_DEF("Температуры по умолчанию"),
    BAD_TEMPERATURE("Температура была введена некорректно"),
    BAD_TEMPERATURE_VALUES("Указанные температуры нарушают правила"),
    CRITICAL_TEMPERATURE_LOW("ОПАСТНОСТЬ РАЗМОРОЗКИ КОНТУРА"),
    CRITICAL_TEMPERATURE_HIGH("ОПАСТНОСТЬ ПОЖАРА"),
    SUCCESS_TEMPERATURE("Температура была успешно задана"),
    CLEAR_ACTUAL_SETTINGS("Настройки температуры для датчика сброшены"),
    PERIOD_GET_ALL("Периоды работы"),
    PERIOD_GET_ACTUAL("Периоды на эту неделю"),
    PERIOD_DELETE_ACTUAL("Сброс периодов на эту неделю"),
    PERIOD_SET_DEFAULT("Задать период по умолчанию"),
    PERIOD_SET_ACTUAL("Задать период на эту неделю"),
    PERIOD_BACK("↩️"),
    PERIOD_BACK_FROM("↩️"),
    PERIOD_HELP_TIME(
            "Нажмите на пункт меню, напишите время начала и окончания оптимального температурного режима в " +
                    "формате 08:00 19:00 (часы:минуты часы:минуты), затем отправьте сообщение. Если в этот день " +
                    "поддерживать оптимальный температурный режим не требуется - укажите 00:00 00:00"),
    EMPTY_PERIODS("Нет периодов на эту неделю,\nиспользуются периоды по умолчанию"),
    CLEAR_ACTUAL_PERIODS("Период работы сброшен для всех датчиков"),
    SUCCESS_TIME("Время нагрева было успешно задано"),
    BAD_TIME("Время указано с ошибкой!"),
    SENSOR_MENU(""),
    SETTINGS_UP(""),
    DEF(""),
    PERIOD_D(""),
    PERIOD_A("");



    private final String message;

    TelegramMenuEnum(String message){
        this.message = message;
    }
}
