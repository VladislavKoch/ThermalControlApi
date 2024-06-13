package ru.vladkochur.thermalControlApi.service.serviceInterface;

import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;

import java.util.List;

public interface TelegramInteractionService {
    public List<TelegramInteraction> getAllTelegramInteractions();
    public TelegramInteraction saveNewTelegramInteraction(TelegramInteraction interaction);
    public void deleteAllTelegramInteractions();
}
