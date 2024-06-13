package ru.vladkochur.thermalControlApi.constants.telegram;

import lombok.Getter;

@Getter
public enum MeasurementsEnum {
    MEASUREMENTS_GET_ALL("Список измерений за неделю"),
    MEASUREMENTS_GET_BY_SERIAL("Список измерений датчика за неделю"),
    MEASUREMENTS_GET_AVG("Средние значения за неделю"),
    MEASUREMENTS_GET_AVG_BY_SERIAL("Средние значения датчика за неделю"),
    MEASUREMENTS_GET_GRAPH("График измерений за неделю"),
    MEASUREMENTS_GET_GRAPH_BY_SERIAL("График измерений датчика за неделю"),
    MEASUREMENTS_GET_DAILY_BY_SERIAL("График измерений датчика за день"),
    MEASUREMENTS_BACK("↩️"),
    MEASUREMENTS_SERIAL_BACK("↩️"),
    BAD_MEASUREMENTS("Измерения не найдены");

    private final String message;

    MeasurementsEnum(String message) {
        this.message = message;
    }
}
