package ru.vladkochur.thermalControlApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "telegram_interaction")
public class TelegramInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @NotNull(message = "You must provide telegram_id")
    private String telegram_id;

    LocalDateTime time;

    public TelegramInteraction(String username, String telegram_id) {
        this.username = username;
        this.telegram_id = telegram_id;
    }
}
