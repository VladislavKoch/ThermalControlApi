package ru.vladkochur.thermalControlApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;

import java.util.Optional;

public interface TelegramInteractionRepository extends JpaRepository<TelegramInteraction, Integer> {

    @Query("from TelegramInteraction where telegram_id = ?1")
    public Optional<TelegramInteraction> findByTelegramId(String id);

}
