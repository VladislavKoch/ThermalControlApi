package ru.vladkochur.thermalControlApi.telegramBot.keyboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vladkochur.thermalControlApi.constants.Weekday;
import ru.vladkochur.thermalControlApi.entity.Sensor;

import java.util.ArrayList;
import java.util.List;

import static ru.vladkochur.thermalControlApi.constants.telegram.MainMenuEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.MeasurementsEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.ResponceEnum.HOW_TO;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorSettingsEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsEnum.*;
import static ru.vladkochur.thermalControlApi.constants.telegram.SensorsPeriodEnum.*;
import static ru.vladkochur.thermalControlApi.telegramBot.telegramUtils.TelegramStyler.sensorToTelegramStyleSingleRow;


@Component
public class InlineKeyboardMaker {
    @Value("${telegram.bot.site}")
    private String url;

    public InlineKeyboardMarkup getMenuInlineKeyboard(boolean isAdmin) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(singleRowInlineMaker(SENSORS.getMessage(), SENSORS.name()));
        if (isAdmin) {
            rowList.add(singleRowInlineMaker(SENSOR_SETTINGS.getMessage(), SENSOR_SETTINGS.name()));
            rowList.add(singleRowInlineMaker(SENSOR_PERIOD.getMessage(), SENSOR_PERIOD.name()));
        }
        rowList.add(singleRowInlineMaker(SENSOR_MEASUREMENTS.getMessage(), SENSOR_MEASUREMENTS.name()));
        rowList.add(singleRowInlineMaker(MY_STATUS.getMessage(), MY_STATUS.name()));
        if (isAdmin) {
            System.out.println();
            rowList.add(singleRowUrlInlineMaker(ADMINISTRATION.getMessage(), url));
        }
        markupInline.setKeyboard(rowList);

        return markupInline;
    }


    public static InlineKeyboardMarkup getSensorsListInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(singleRowInlineMaker(SENSOR_GET_INTERACTIONS.getMessage(),
                SENSOR_GET_INTERACTIONS.name()));
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker(name, "SENSOR_MENU_" + sensor.getSerial()));
        }
        rowList.add(singleRowInlineMaker(SENSORS_BACK.getMessage(), SENSORS_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorInlineKeyboard(boolean isAdmin, String serial) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(singleRowInlineMaker(SENSOR_GET_DATA.getMessage(), enrichEnum(SENSOR_GET_DATA, serial)));
        if (isAdmin) {
            rowList.add(singleRowInlineMaker(HOW_TO.getMessage(), SENSOR_HELP_NAME_UPDATE.name()));
            rowList.add(singleRowSupportInlineMaker(SENSOR_UPDATE.getMessage(),
                    String.format("%s_%s_", SENSOR_UPDATE.name(), serial)));
            rowList.add(singleRowInlineMaker(SENSOR_DELETE.getMessage(), enrichEnum(SENSOR_DELETE, serial)));

        }
        rowList.add(singleRowInlineMaker(SENSOR_BACK.getMessage(), SENSOR_BACK.name()));

        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsInteractionInlineKeyboard(boolean isAdmin, List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        if (isAdmin) {
            rowList.add(singleRowInlineMaker(SENSOR_CLEAR_INTERACTIONS.getMessage(), SENSOR_CLEAR_INTERACTIONS.name()));
        }
        for (Sensor sensor : sensors) {
            String name = "serial : " + sensor.getSerial();
            rowList.add(singleRowInlineMaker(name, "SENSOR_MENU_" + sensor.getSerial()));
        }
        rowList.add(singleRowInlineMaker(SENSOR_BACK.getMessage(), SENSOR_BACK.name()));

        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getMeasurementsInlineKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_DAILY_BY_SERIAL.getMessage(),
                MEASUREMENTS_GET_DAILY_BY_SERIAL.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_GRAPH.getMessage(),
                MEASUREMENTS_GET_GRAPH.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_GRAPH_BY_SERIAL.getMessage(),
                MEASUREMENTS_GET_GRAPH_BY_SERIAL.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_ALL.getMessage(),
                MEASUREMENTS_GET_ALL.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_BY_SERIAL.getMessage(),
                MEASUREMENTS_GET_BY_SERIAL.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_AVG.getMessage(),
                MEASUREMENTS_GET_AVG.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_GET_AVG_BY_SERIAL.getMessage(),
                MEASUREMENTS_GET_AVG_BY_SERIAL.name()));
        rowList.add(singleRowInlineMaker(MEASUREMENTS_BACK.getMessage(), MEASUREMENTS_BACK.name()));

        markupInline.setKeyboard(rowList);
        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForMeasurementsInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker
                    (name, String.format("%s_%s", MEASUREMENTS_GET_BY_SERIAL.name(), sensor.getSerial())));
        }
        rowList.add(singleRowInlineMaker(MEASUREMENTS_SERIAL_BACK.getMessage(), MEASUREMENTS_SERIAL_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForMeasurementsAvgInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker
                    (name, String.format("%s_%s", MEASUREMENTS_GET_AVG_BY_SERIAL.name(), sensor.getSerial())));
        }
        rowList.add(singleRowInlineMaker(MEASUREMENTS_SERIAL_BACK.getMessage(), MEASUREMENTS_SERIAL_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForGraphAvgInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker
                    (name, String.format("%s_%s", MEASUREMENTS_GET_GRAPH_BY_SERIAL.name(), sensor.getSerial())));
        }
        rowList.add(singleRowInlineMaker(MEASUREMENTS_SERIAL_BACK.getMessage(), MEASUREMENTS_SERIAL_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForDailyGraphInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker
                    (name, String.format("%s_%s", MEASUREMENTS_GET_DAILY_BY_SERIAL.name(), sensor.getSerial())));
        }
        rowList.add(singleRowInlineMaker(MEASUREMENTS_SERIAL_BACK.getMessage(), MEASUREMENTS_SERIAL_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSettingsInlineKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(singleRowInlineMaker(SETTINGS_GET_ALL.getMessage(), SETTINGS_GET_ALL.name()));
        rowList.add(singleRowInlineMaker(SETTINGS_GET_BY_SERIAL.getMessage(), SETTINGS_GET_BY_SERIAL.name()));
        rowList.add(singleRowInlineMaker(SETTINGS_UPDATE.getMessage(), SETTINGS_UPDATE.name()));
        rowList.add(singleRowInlineMaker(SETTINGS_DELETE.getMessage(), SETTINGS_DELETE.name()));
        rowList.add(singleRowInlineMaker(SETTINGS_BACK.getMessage(), SETTINGS_BACK.name()));

        markupInline.setKeyboard(rowList);
        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForSettingsInlineKeyboard(List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Sensor sensor : sensors) {
            String name = sensorToTelegramStyleSingleRow(sensor);
            rowList.add(singleRowInlineMaker
                    (name, String.format("%s_%s", SETTINGS_GET_BY_SERIAL.name(), sensor.getSerial())));
        }
        rowList.add(singleRowInlineMaker(SETTINGS_SENSORS_BACK.getMessage(), SETTINGS_SENSORS_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getSettingsUpdateInlineKeyboard(boolean isAdmin, List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if (isAdmin) {
            rowList.add(singleRowInlineMaker(HOW_TO.getMessage(), SETTINGS_HELP_TEMPERATURE.name()));
            rowList.add(singleRowSupportInlineMaker(SETTINGS_UP_DEF_.getMessage(), SETTINGS_UP_DEF_.name()));
            for (Sensor sensor : sensors) {
                String name = sensorToTelegramStyleSingleRow(sensor);
                rowList.add(singleRowSupportInlineMaker(name, String.format("SETTINGS_UP_%s_", sensor.getSerial())));
            }
        }
        rowList.add(singleRowInlineMaker(SETTINGS_SENSORS_BACK.getMessage(), SETTINGS_SENSORS_BACK.name()));
        markupInline.setKeyboard(rowList);
        return markupInline;
    }

    public static InlineKeyboardMarkup getSensorsListForSettingsDeleteInlineKeyboard(boolean isAdmin, List<Sensor> sensors) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if (isAdmin) {
            for (Sensor sensor : sensors) {
                String name = sensorToTelegramStyleSingleRow(sensor);
                rowList.add(singleRowInlineMaker
                        (name, String.format("%s_%s", SETTINGS_DELETE.name(), sensor.getSerial())));
            }
        }
        rowList.add(singleRowInlineMaker(SETTINGS_SENSORS_BACK.getMessage(), SETTINGS_SENSORS_BACK.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getPeriodInlineKeyboard(boolean isAdmin) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(singleRowInlineMaker(PERIOD_GET_ALL.getMessage(), PERIOD_GET_ALL.name()));
        rowList.add(singleRowInlineMaker(PERIOD_GET_ACTUAL.getMessage(), PERIOD_GET_ACTUAL.name()));
        if (isAdmin) {
            rowList.add(singleRowInlineMaker(PERIOD_SET_DEFAULT.getMessage(), PERIOD_SET_DEFAULT.name()));
            rowList.add(singleRowInlineMaker(PERIOD_SET_ACTUAL.getMessage(), PERIOD_SET_ACTUAL.name()));
            rowList.add(singleRowInlineMaker(PERIOD_DELETE_ACTUAL.getMessage(), PERIOD_DELETE_ACTUAL.name()));
        }
        rowList.add(singleRowInlineMaker(PERIOD_BACK.getMessage(), PERIOD_BACK.name()));

        markupInline.setKeyboard(rowList);
        return markupInline;
    }

    public static InlineKeyboardMarkup getWeekdaySetDefaultInlineKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(singleRowInlineMaker(HOW_TO.getMessage(), PERIOD_HELP_TIME.name()));

        for (Weekday weekday : Weekday.values()) {
            rowList.add(singleRowSupportInlineMaker(weekday.getMessage(),
                    String.format("PERIOD_D_%s_", weekday.ordinal())));
        }
        rowList.add(singleRowInlineMaker(PERIOD_BACK_FROM.getMessage(), PERIOD_BACK_FROM.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    public static InlineKeyboardMarkup getWeekdaySetActualInlineKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(singleRowInlineMaker(HOW_TO.getMessage(), PERIOD_HELP_TIME.name()));

        for (Weekday weekday : Weekday.values()) {
            rowList.add(singleRowSupportInlineMaker(weekday.getMessage(),
                    String.format("PERIOD_A_%s_", weekday.ordinal())));
        }
        rowList.add(singleRowInlineMaker(PERIOD_BACK_FROM.getMessage(), PERIOD_BACK_FROM.name()));
        markupInline.setKeyboard(rowList);

        return markupInline;
    }

    private static List<InlineKeyboardButton> singleRowInlineMaker(String text, String callback) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callback);
        rowInline.add(inlineKeyboardButton);
        return rowInline;
    }

    private static List<InlineKeyboardButton> singleRowUrlInlineMaker(String text, String url) {
        List<InlineKeyboardButton> rowInline = singleRowInlineMaker(text, null);
        rowInline.get(0).setUrl(url);
        return rowInline;
    }

    private static List<InlineKeyboardButton> singleRowSupportInlineMaker(String text, String inlineQuery) {
        List<InlineKeyboardButton> rowInline = singleRowInlineMaker(text, null);
        rowInline.get(0).setSwitchInlineQueryCurrentChat(inlineQuery);
        return rowInline;
    }

    private static String enrichEnum(Enum<?> e, String serial) {
        return String.format("%s_%s", e.name(), serial);
    }

}