package ru.vladkochur.thermalControlApi.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;

import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {
    private final MyUserRepository myUserRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return MyUser.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MyUser user = (MyUser) o;
        if (user.getRoles() == null) {
            return;
        }
        boolean isOk = Arrays.stream(user.getRoles().split(", ")).allMatch(x ->
                x.equals("ROLE_ADMIN") || x.equals("ROLE_USER") || x.equals("ROLE_SENSOR"));
        if (!isOk) {
            errors.rejectValue("roles", "",
                    "Укажите корректные роли пользователя в формате :" +
                            " \"ROLE_USER\" или \"ROLE_USER, ROLE_ADMIN, ROLE_SENSOR\"");
        }
    }

    public void validateUpdate(MyUser updatedUser, Errors errors, int id) {
        MyUser oldUser = myUserRepository.findById(id).orElseThrow(MyUserNotFoundException::new);

        String updatedLogin = updatedUser.getLogin();
        String oldLogin = oldUser.getLogin();
        String updatedTelegram = updatedUser.getTelegram();
        String oldTelegram = oldUser.getTelegram();

        if (!Objects.equals(updatedLogin, oldLogin)) {
            if (myUserRepository.findByLogin(updatedLogin).isPresent()) {
                errors.rejectValue("login", "", "Этот логин уже используется");
            }
        }
        if (!Objects.equals(updatedTelegram, oldTelegram) && updatedTelegram != null) {
            if (myUserRepository.findByTelegram(updatedTelegram).isPresent()) {
                errors.rejectValue("telegram", "",
                        "Этот telegram уже привязан к другому аккаунту");
            }
        }
    }

    public void validateSave(MyUser user, Errors errors) {
        boolean isExists = myUserRepository.findByLogin(user.getLogin()).isPresent();
        if (isExists) {
            errors.rejectValue("login", "", "Этот логин уже используется");
        }
        if (user.getTelegram() != null && myUserRepository.findByTelegram(user.getTelegram()).isPresent()) {
            errors.rejectValue("telegram", "",
                    "Этот telegram уже привязан к другому аккаунту");
        }
    }
}
