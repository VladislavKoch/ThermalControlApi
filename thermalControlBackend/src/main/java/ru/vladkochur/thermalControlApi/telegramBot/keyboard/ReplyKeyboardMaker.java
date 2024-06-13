package ru.vladkochur.thermalControlApi.telegramBot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vladkochur.thermalControlApi.constants.telegram.ResponceEnum;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplyKeyboardMaker {
    private static ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getAuthorizationKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(ResponceEnum.WANT_TO_LOG_IN.name()));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/menu"));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

}