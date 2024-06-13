package ru.vladkochur.thermalControlApi.constants;

import lombok.Getter;

@Getter
public enum Weekday {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье");

    private final String message;

    Weekday(String message) {
        this.message = message;
    }
}
