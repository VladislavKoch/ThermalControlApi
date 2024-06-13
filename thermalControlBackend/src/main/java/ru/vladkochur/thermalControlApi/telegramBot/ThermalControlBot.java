package ru.vladkochur.thermalControlApi.telegramBot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vladkochur.thermalControlApi.configuration.telegramBotConfig.BotProperties;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserService;
import ru.vladkochur.thermalControlApi.telegramBot.handler.TelegramCommunicationService;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThermalControlBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final TelegramCommunicationService telegramCommunicationService;
    private final MyUserService userService;

    @Override
    public String getBotToken() {
        return botProperties.token();
    }

    @Override
    public String getBotUsername() {
        return botProperties.name();
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod<?> botApiMethod = telegramCommunicationService.communicationReceiver(update);
        try {
            execute(botApiMethod);
        } catch (TelegramApiException e) {
             log.error("Error caught when telegram response message sending :" + e.getMessage());
        }
    }

    public void sendMessageToAllUsers(String message){
        List<String> userTelegramChatIds = userService.findAllUsers().stream().map(MyUser::getTelegram)
                .filter(Objects::nonNull).toList();
        for (String chatId : userTelegramChatIds) {
            try {
                execute(new SendMessage(chatId, message));
            } catch (TelegramApiException e) {
                log.error("Error caught when telegram response message sending :" + e.getMessage());
            }
        }
    }
}
