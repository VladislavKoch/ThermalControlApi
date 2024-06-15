package ru.vladkochur.thermalControlApi.configuration.telegramBotConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vladkochur.thermalControlApi.telegramBot.ThermalControlBot;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfiguration {
    private final ThermalControlBot bot;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException ex){
            log.error("Error caught when telegram bot tried to register :" + ex.getMessage());
        }
    }
}
