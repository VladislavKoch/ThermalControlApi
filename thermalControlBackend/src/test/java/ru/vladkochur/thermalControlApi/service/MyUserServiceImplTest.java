package ru.vladkochur.thermalControlApi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;


@ExtendWith(MockitoExtension.class)
class MyUserServiceImplTest {
    @Mock
    private MyUserRepository myUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MyUserServiceImpl myUserService;


    @Test
    @DisplayName("Test get all users functionality")
    public void givenThreeUsersInDB_whenFindAllUsers_thenRepositoryIsCalled() {
        //given
        List<MyUser> users = List.of( getFirstUserPersisted(),  getSecondUserPersisted(),
                 getThirdUserPersisted());
        BDDMockito.given(myUserRepository.findAllSensorsFirst()).willReturn(users);
        //when
        List<MyUser> obtainedUsers = myUserService.findAllUsers();
        //then
        assertThat(obtainedUsers).isNotEmpty();
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findAllSensorsFirst();
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenId_whenFindUserById_thenRepositoryIsCalled() {
        //given
        MyUser user =  getFirstUserPersisted();
        BDDMockito.given(myUserRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        //when
        MyUser obtainedUser = myUserService.findUserById(1);
        //then
        assertThat(obtainedUser).isEqualTo(user);
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Test get user by incorrect id functionality")
    public void givenIncorrectId_whenFindUserById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(myUserRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(MyUserNotFoundException.class, () -> myUserService.findUserById(1));
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Test save user by id functionality")
    public void givenUser_whenSaveUser_thenRepositoryIsCalled() {
        //given
        MyUser user =  getFirstUserTransient();
        MyUser user1 =  getFirstUserPersisted();
        BDDMockito.given(myUserRepository.save(any(MyUser.class))).willReturn(user1);
        BDDMockito.given(passwordEncoder.encode(anyString())).willReturn("Password");
        //when
        myUserService.save(user);
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).save(any(MyUser.class));
        BDDMockito.verify(passwordEncoder, BDDMockito.times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Test update user password by id functionality")
    public void givenUserWithPassword_whenUpdateUser_thenRepositoryIsCalled() {
        //given
        MyUser user =  getFirstUserPersisted();
        BDDMockito.given(myUserRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        BDDMockito.given(passwordEncoder.encode(anyString())).willReturn("Password");
        BDDMockito.given(myUserRepository.save(any(MyUser.class))).willReturn(user);
        //when
        myUserService.updateUser(1, user, true);
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findById(anyInt());
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).save(any(MyUser.class));
        BDDMockito.verify(passwordEncoder, BDDMockito.times(1)).encode(anyString());
    }

    @Test
    @DisplayName("Test update user data by id functionality")
    public void givenUserWithData_whenUpdateUser_thenRepositoryIsCalled() {
        //given
        MyUser user =  getFirstUserPersisted();
        BDDMockito.given(myUserRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        BDDMockito.given(myUserRepository.save(any(MyUser.class))).willReturn(user);
        //when
        myUserService.updateUser(1, user, false);
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findById(anyInt());
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).save(any(MyUser.class));
        BDDMockito.verify(passwordEncoder, BDDMockito.never()).encode(anyString());
    }

    @Test
    @DisplayName("Test update user by incorrect id functionality")
    public void givenIncorrectId_whenUpdateUser_thenExceptionIsThrown() {
        //given
        BDDMockito.given(myUserRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        MyUser user =  getFirstUserPersisted();
        assertThrows(MyUserNotFoundException.class, () -> myUserService.updateUser(1, user, false));
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findById(anyInt());
        BDDMockito.verify(myUserRepository, BDDMockito.never()).save(any(MyUser.class));
        BDDMockito.verify(passwordEncoder, BDDMockito.never()).encode(anyString());
    }

    @Test
    @DisplayName("Test delete user by id functionality")
    public void givenId_whenDeleteUser_thenRepositoryIsCalled() {
        //given

        //when
        myUserService.delete(1);
        //then
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test find user by telegram id functionality")
    public void givenId_whenFindUserByTelegramId_thenRepositoryIsCalled() {
        //given
        MyUser user =  getFirstUserPersisted();
        BDDMockito.given(myUserRepository.findByTelegram(anyString())).willReturn(Optional.ofNullable(user));
        //when
        Optional <MyUser> obtainedUser = myUserService.findUserByTelegramId("123");
        //then
        assertThat(obtainedUser).isNotEmpty();
        BDDMockito.verify(myUserRepository, BDDMockito.times(1)).findByTelegram(anyString());
    }
}