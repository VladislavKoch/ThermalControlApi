package ru.vladkochur.thermalControlApi.service.securtyService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladkochur.thermalControlApi.configuration.securityConfig.MyUserDetails;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyUserServiceImpl implements MyUserService {
    private final MyUserRepository myUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<MyUser> findAllUsers() {
        return myUserRepository.findAllSensorsFirst();
    }

    @Override
    public MyUser findUserById(int id) {
        return myUserRepository.findById(id).orElseThrow(MyUserNotFoundException::new);
    }

    @Override
    @Transactional
    public UserDetails save(MyUser myUser) {
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        if (myUser.getTelegram() != null) {
            myUser.setTelegram(myUser.getTelegram().isBlank() ? null : myUser.getTelegram());
        }
        return new MyUserDetails(myUserRepository.save(myUser));
    }

    @Override
    @Transactional
    public void updateUser(int id, MyUser updatedUser, boolean updatePassword) {
        MyUser obtainedUser = myUserRepository.findById(id).orElseThrow(MyUserNotFoundException::new);
        if (updatePassword) {
            obtainedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        } else {
            obtainedUser.setLogin(updatedUser.getLogin());
            obtainedUser.setRoles(updatedUser.getRoles());
            if (updatedUser.getTelegram() != null) {
                obtainedUser.setTelegram(updatedUser.getTelegram().isBlank() ? null : updatedUser.getTelegram());
            }
        }
        myUserRepository.save(obtainedUser);
    }

    @Override
    @Transactional
    public void delete(int id) {
        myUserRepository.deleteById(id);
    }

    @Override
    public Optional<MyUser> findUserByTelegramId(String telegramId) {
        return myUserRepository.findByTelegram(telegramId);
    }
}
