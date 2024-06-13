package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladkochur.thermalControlApi.entity.TelegramInteraction;
import ru.vladkochur.thermalControlApi.repository.TelegramInteractionRepository;
import ru.vladkochur.thermalControlApi.util.DataUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TelegramInteractionServiceImplTest {
    @Mock
    private TelegramInteractionRepository telegramInteractionRepository;
    @InjectMocks
    private TelegramInteractionServiceImpl telegramInteractionService;

    @Test
    @DisplayName("Test get all telegram interactions functionality")
    public void givenThreeInteractionInDB_whenGetAllTelegramInteractions_thenRepositoryIsCalled() {
        //given
        List<TelegramInteraction> interactions = List.of(DataUtils.getFirstTelegramInteractionPersisted(),
                DataUtils.getFirstTelegramInteractionPersisted(), DataUtils.getFirstTelegramInteractionPersisted());
        BDDMockito.given(telegramInteractionRepository.findAll()).willReturn(interactions);
        //when
        List<TelegramInteraction> obtainedInteractions = telegramInteractionService.getAllTelegramInteractions();
        //then
        assertThat(obtainedInteractions).isNotEmpty();
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Test save new telegram interactions functionality")
    public void givenTelegramInteraction_whenSaveNewTelegramInteraction_thenRepositoryIsCalled() {
        //given
        TelegramInteraction interaction = DataUtils.getFirstTelegramInteractionPersisted();
        BDDMockito.given(telegramInteractionRepository.save(any(TelegramInteraction.class))).willReturn(interaction);
        BDDMockito.given(telegramInteractionRepository.findByTelegramId(anyString())).willReturn(Optional.empty());
        //when
        TelegramInteraction obtainedInteraction = telegramInteractionService.saveNewTelegramInteraction(interaction);
        //then
        assertThat(obtainedInteraction).isNotNull();
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.times(1))
                .save(any(TelegramInteraction.class));
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.times(1))
                .findByTelegramId(anyString());
    }

    @Test
    @DisplayName("Test save already existed telegram interactions functionality")
    public void givenExistedTelegramInteraction_whenSaveNewTelegramInteraction_thenRepositoryIsCalled() {
        //given
        TelegramInteraction interaction = DataUtils.getFirstTelegramInteractionPersisted();
        BDDMockito.given(telegramInteractionRepository.findByTelegramId(anyString())).willReturn(
                Optional.ofNullable(interaction));
        //when
        TelegramInteraction obtainedInteraction = telegramInteractionService.saveNewTelegramInteraction(interaction);
        //then
        assertThat(obtainedInteraction).isNotNull();
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.never()).save(any(TelegramInteraction.class));
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.times(1))
                .findByTelegramId(anyString());
    }

    @Test
    @DisplayName("Test delete all telegram interactions functionality")
    public void givenDeleteAllRequest_whenDeleteAllTelegramInteractions_thenRepositoryIsCalled() {
        //given

        //when
        telegramInteractionService.deleteAllTelegramInteractions();
        //then
        BDDMockito.verify(telegramInteractionRepository, BDDMockito.times(1)).deleteAll();
    }
}