package ru.vladkochur.thermalControlApi.service.serviceInterface;

import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;

import java.util.List;
@Transactional
public interface TelegramInteractionService {
    @Transactional(readOnly = true)
    public List<TelegramInteraction> getAllTelegramInteractions();

    public TelegramInteraction saveNewTelegramInteraction(TelegramInteraction interaction);

    public void deleteAllTelegramInteractions();
}
