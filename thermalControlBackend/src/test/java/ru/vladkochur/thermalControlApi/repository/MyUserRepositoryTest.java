package ru.vladkochur.thermalControlApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.vladkochur.thermalControlApi.entity.MyUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@DataJpaTest
class MyUserRepositoryTest {
    @Autowired
    private MyUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test find user by login functionality")
    public void givenLogin_whenFindByLogin_thenMyUserOptionalIsReturned() {
        //given
        MyUser user =  getFirstUserTransient();
        userRepository.save(user);
        //when
        MyUser obtainedUser = userRepository.findByLogin(user.getLogin()).orElse(null);
        //then
        assertThat(obtainedUser).isNotNull();
        assertThat(obtainedUser.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("Test find user by telegram functionality")
    public void givenTelegram_whenFindByTelegram_thenMyUserOptionalIsReturned() {
        //given
        MyUser user =  getFirstUserTransient();
        userRepository.save( getFirstUserTransient());
        //when
        MyUser obtainedUser = userRepository.findByTelegram(user.getTelegram()).orElse(null);
        //then
        assertThat(obtainedUser).isNotNull();
        assertThat(obtainedUser.getTelegram()).isEqualTo(user.getTelegram());
    }

    @Test
    @DisplayName("Test find all users with sensors first functionality")
    public void givenThreeUserInDB_whenFindAllSensorsFirst_thenListWithSensorsFirstIsReturned() {
        //given
        userRepository.save( getFirstUserTransient());
        userRepository.save( getSecondUserTransient());
        userRepository.save( getThirdUserTransient());
        //when
        List<MyUser> users = userRepository.findAllSensorsFirst();
        //then
        assertThat(users).isNotNull();
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getRoles()).isEqualTo("ROLE_SENSOR");
    }

}