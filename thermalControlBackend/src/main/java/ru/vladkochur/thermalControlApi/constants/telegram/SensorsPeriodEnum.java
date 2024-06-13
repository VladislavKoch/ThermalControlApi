package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum SensorsPeriodEnum {
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
    BAD_TIME("Время указано с ошибкой!");

    private final String message;

    SensorsPeriodEnum(String message) {
        this.message = message;
    }
}
