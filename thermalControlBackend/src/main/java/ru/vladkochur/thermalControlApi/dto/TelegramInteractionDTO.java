package ru.vladkochur.thermalControlApi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelegramInteractionDTO {

    private String username;

    @NotNull(message = "You must provide telegram_id")
    private String telegram_id;

    LocalDateTime time;

}
