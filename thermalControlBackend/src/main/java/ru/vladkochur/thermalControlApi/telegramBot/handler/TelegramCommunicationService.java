package ru.vladkochur.thermalControlApi.telegramBot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserService;
import ru.vladkochur.thermalControlApi.service.serviceInterface.TelegramInteractionService;

import java.util.Optional;

import static ru.vladkochur.thermalControlApi.constants.telegram.ResponceEnum.*;
import static ru.vladkochur.thermalControlApi.telegramBot.keyboard.ReplyKeyboardMaker.getAuthorizationKeyboard;

@Service
@RequiredArgsConstructor
public class TelegramCommunicationService {
    private final MyUserService userService;
    private final TelegramInteractionService interactionService;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    public BotApiMethod<?> communicationReceiver(Update update) {
        String telegramId = null;
        String username = null;
        Long chatId = 0L;
        Optional<MyUser> obtainedUser = Optional.empty();
        boolean isAdmin;

        if (update.hasMessage()) {
            username = update.getMessage().getChat().getUserName();
            telegramId = update.getMessage().getChat().getId().toString();
            obtainedUser = userService.findUserByTelegramId(telegramId);
            chatId = update.getMessage().getChatId();
        }
        else if (update.hasCallbackQuery()) {
            username = update.getCallbackQuery().getMessage().getChat().getUserName();
            telegramId = update.getCallbackQuery().getMessage().getChat().getId().toString();
            obtainedUser = userService.findUserByTelegramId(telegramId);
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        else if (update.hasEditedMessage()) {
            chatId = update.getEditedMessage().getChatId();
            return new SendMessage(chatId.toString(), BAD_COMMAND.getMessage());
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        //Авторизация
        if (obtainedUser.isEmpty()) {

            if (update.hasMessage() && update.getMessage().hasText() &&
                    update.getMessage().getText().equals(WANT_TO_LOG_IN.name())) {
                interactionService.saveNewTelegramInteraction(new TelegramInteraction(username, telegramId));
                sendMessage.setText(AUTHORIZATION.getMessage());

            } else{
                sendMessage.setText(WANT_TO_LOG_IN.getMessage());
                sendMessage.setReplyMarkup(getAuthorizationKeyboard());
            }
            return sendMessage;

        } else {
            isAdmin = obtainedUser.get().getRoles().contains("ROLE_ADMIN");
        }

        //Бизнес
        if (update.hasMessage() && update.getMessage().hasText()) {
            return messageHandler.answerMessage(sendMessage, update.getMessage(), isAdmin);

        } else if (update.hasCallbackQuery()) {
            return callbackQueryHandler.answerCallback(sendMessage, update.getCallbackQuery(), isAdmin);
        }

        sendMessage.setText(BAD_FORMAT.getMessage());
        return sendMessage;
    }
}
