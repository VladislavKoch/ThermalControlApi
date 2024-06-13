package ru.vladkochur.thermalControlApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TelegramInteractionRepositoryTest {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private TelegramInteractionRepository telegramInteractionRepository;

    @BeforeEach
    void setUp() {
        telegramInteractionRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    @DisplayName("Test delete settings by serial functionality")
    public void givenTelegramId_whenFindByTelegramId_thenTelegramInteractionOptionalIsReturned() {
        //given
        TelegramInteraction interaction = DataUtils.getFirstTelegramInteractionTransient();
        telegramInteractionRepository.save(interaction);
        //when
        TelegramInteraction obtainedInteraction =
                telegramInteractionRepository.findByTelegramId(interaction.getTelegram_id()).orElse(null);
        //then
        assertThat(obtainedInteraction).isNotNull();
        assertThat(telegramInteractionRepository.findAll().size()).isEqualTo(1);
    }
}