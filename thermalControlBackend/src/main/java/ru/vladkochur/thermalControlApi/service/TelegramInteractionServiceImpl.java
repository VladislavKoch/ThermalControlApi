package ru.vladkochur.thermalControlApi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;
import ru.vladkochur.thermalControlApi.repository.TelegramInteractionRepository;
import ru.vladkochur.thermalControlApi.service.serviceInterface.TelegramInteractionService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramInteractionServiceImpl implements TelegramInteractionService {

    private final TelegramInteractionRepository telegramInteractionRepository;

    @Override
    public List<TelegramInteraction> getAllTelegramInteractions() {
        return telegramInteractionRepository.findAll();
    }

    @Override
    public TelegramInteraction saveNewTelegramInteraction(TelegramInteraction interaction) {
        boolean isExist = telegramInteractionRepository.findByTelegramId(interaction.getTelegram_id()).isPresent();
        if (isExist) {
            return interaction;
        } else {
            interaction.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            return telegramInteractionRepository.save(interaction);
        }
    }

    @Override
    public void deleteAllTelegramInteractions() {
        telegramInteractionRepository.deleteAll();
    }
}
