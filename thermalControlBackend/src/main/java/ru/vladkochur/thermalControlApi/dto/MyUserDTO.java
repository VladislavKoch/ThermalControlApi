package ru.vladkochur.thermalControlApi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyUserDTO {

    private Integer id;

    @NotNull(message = "Необходимо указать логин")
    @Size(min = 3, max = 100, message = "Логин должен состоять минимум из 3 и максимум из 100 символов")
    private String login;

    @NotNull(message = "Необходимо указать пароль")
    @Size(min = 3, max = 100, message = "Пароль должен состоять минимум из 3 и максимум из 100 символов")
    private String password;

    private String telegram;

    @NotNull(message = "Необходимо указать роли")
    private String roles;
}
