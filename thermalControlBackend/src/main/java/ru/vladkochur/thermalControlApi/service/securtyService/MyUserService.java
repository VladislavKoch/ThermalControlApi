package ru.vladkochur.thermalControlApi.service.securtyService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.entity.MyUser;

import java.util.List;
import java.util.Optional;
@Transactional(readOnly = true)
public interface MyUserService extends UserDetailsService {
    List<MyUser> findAllUsers();

    public MyUser findUserById(int id);

    @Transactional
    public MyUser save(MyUser myUser);

    @Transactional
    public void updateUser(int id, MyUser updatedUser, boolean updatePassword);

    @Transactional
    public void delete(int id);

    public Optional<MyUser> findUserByTelegramId(String telegramId);
}
