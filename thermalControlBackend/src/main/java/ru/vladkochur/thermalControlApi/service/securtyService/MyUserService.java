package ru.vladkochur.thermalControlApi.service.securtyService;

import org.springframework.security.core.userdetails.UserDetails;
import ru.vladkochur.thermalControlApi.entity.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserService {
    List<MyUser> findAllUsers();

    public MyUser findUserById(int id);

    public UserDetails save(MyUser myUser);

    public void updateUser(int id, MyUser updatedUser, boolean updatePassword);

    public void delete(int id);

    public Optional<MyUser> findUserByTelegramId(String telegramId);
}
