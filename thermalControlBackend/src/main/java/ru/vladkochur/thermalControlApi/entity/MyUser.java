package ru.vladkochur.thermalControlApi.entity;


import jakarta.persistence.*;
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
@Entity
@Table(name = "my_user")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "You must provide sensor serial number")
    @Size(min = 3, max = 100, message = "Login must have from 3 up to 100 symbols")
    private String login;

    @NotNull(message = "You must provide sensor serial number")
    @Size(min = 3, max = 100, message = "Password must have from 3 up to 100 symbols")
    private String password;

    private String telegram;

    @NotNull
    private String roles;
}
