package ru.vladkochur.thermalControlApi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.vladkochur.thermalControlApi.configuration.securityConfig.MyUserDetails;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.MyUserDTO;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.exception.MyUserNotFoundException;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;
import ru.vladkochur.thermalControlApi.service.TelegramInteractionServiceImpl;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserServiceImpl;
import ru.vladkochur.thermalControlApi.util.DataUtils;
import ru.vladkochur.thermalControlApi.utils.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.vladkochur.thermalControlApi.util.testConstraints.myUserControllerTestConstraints.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = MyUserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class MyUserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private DtoConverter converter;
    @SpyBean
    private UserValidator userValidator;
    @MockBean
    private TelegramInteractionServiceImpl telegramInteractionService;
    @MockBean
    private MyUserServiceImpl myUserService;
    @MockBean
    private MyUserRepository userRepository;

    @Test
    @DisplayName("Test get all users functionality")
    public void givenThreeUsers_whenGetAllUsers_thenViewResponse() throws Exception {
        //given
        List<MyUser> users = DataUtils.getUsersPersisted();
        BDDMockito.given(myUserService.findAllUsers()).willReturn(users);
        BDDMockito.given(telegramInteractionService.getAllTelegramInteractions())
                .willReturn(List.of(DataUtils.getFirstTelegramInteractionPersisted()));
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/administration/users"));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(USERS_LIST.getMessage())))
                .andExpect(content().string(containsString(TELEGRAM_INTERACTION_LIST.getMessage())))
                .andExpect(content().string(containsString(CLEAR_TELEGRAM_INTERACTION.getMessage())))
                .andExpect(content().string(containsString(CREATE_USER.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.times(1)).findAllUsers();
        BDDMockito.verify(telegramInteractionService, BDDMockito.times(1))
                .getAllTelegramInteractions();
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenUserId_whenShowUser_thenViewResponse() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(myUserService.findUserById(anyInt())).willReturn(user);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/administration/users/" + user.getId()));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SHOW_USER.getMessage())))
                .andExpect(content().string(containsString(DELETE_USER.getMessage())))
                .andExpect(content().string(containsString(EDIT_USER.getMessage())))
                .andExpect(content().string(containsString(EDIT_USER_PASSWORD.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.times(1)).findUserById(anyInt());
    }

    @Test
    @DisplayName("Test get user by incorrect id functionality")
    public void givenIncorrectUserId_whenShowUser_thenRedirect() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(myUserService.findUserById(anyInt())).willThrow(MyUserNotFoundException.class);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/administration/users/" + user.getId()));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));
    }

    @Test
    @DisplayName("Test get new user form functionality")
    public void givenNewUserRequest_whenGetNewUserForm_thenNewUserViewResponse() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/administration/users/new"));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(NEW_USER.getMessage())))
                .andExpect(content().string(containsString(BACK_TO_ALL_USERS.getMessage())));
    }

    @Test
    @DisplayName("Test save new user functionality")
    public void givenUserDTO_whenSaveNewUser_thenServiceIsCalledAndRedirect() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(myUserService.save(any(MyUser.class))).willReturn(new MyUserDetails(user));
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.times(1)).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with empty login functionality")
    public void givenUserDTOWithEmptyName_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setLogin(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_LOGIN.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with incorrect login functionality")
    public void givenUserDTOWithIncorrectName_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setLogin("Us");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_NAME.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with duplicate login functionality")
    public void givenUserDTOWithDuplicateLogin_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(userRepository.findByLogin(anyString())).willReturn(Optional.of(user));

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_LOGIN.getMessage())));
        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with empty password functionality")
    public void givenUserDTOWithEmptyPassword_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setPassword(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_PASSWORD.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with incorrect password functionality")
    public void givenUserDTOWithIncorrectPassword_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setPassword("Ps");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_PASSWORD.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with duplicate telegram functionality")
    public void givenUserDTOWithDuplicateTelegram_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(userRepository.findByLogin(anyString())).willReturn(Optional.empty());
        BDDMockito.given(userRepository.findByTelegram(anyString())).willReturn(Optional.of(user));

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_TELEGRAM.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with empty roles functionality")
    public void givenUserDTOWithEmptyRoles_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setRoles(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_ROLES.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test save new user with incorrect roles functionality")
    public void givenUserDTOWithIncorrectRoles_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        dto.setRoles("INCORRECT_ROLE");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_ROLES.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).save(any(MyUser.class));
    }

    @Test
    @DisplayName("Test get edit user form functionality")
    public void givenUser_whenGetEditUserForm_thenEditUserViewResponse() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(myUserService.findUserById(anyInt())).willReturn(user);
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit", 1)));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SHOW_EDIT_USER.getMessage())))
                .andExpect(content().string(containsString(BACK_TO_ALL_USERS.getMessage())));
    }

    @Test
    @DisplayName("Test get edit user form with incorrect id functionality")
    public void givenIncorrectId_whenGetEditUserForm_thenRedirect() throws Exception {
        //given
        BDDMockito.given(myUserService.findUserById(anyInt())).willThrow(MyUserNotFoundException.class);
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit", 1)));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));
    }

    @Test
    @DisplayName("Test get edit user password form functionality")
    public void givenUser_whenGetEditUserPasswordForm_thenEditUserViewResponse() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        BDDMockito.given(myUserService.findUserById(anyInt())).willReturn(user);
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit_password", 1)));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SHOW_EDIT_USER_PASSWORD.getMessage())))
                .andExpect(content().string(containsString(BACK_TO_ALL_USERS.getMessage())));
    }

    @Test
    @DisplayName("Test get edit user password form with incorrect id functionality")
    public void givenIncorrectId_whenGetEditUserPasswordForm_thenRedirect() throws Exception {
        //given
        BDDMockito.given(myUserService.findUserById(anyInt())).willThrow(MyUserNotFoundException.class);
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit_password", 1)));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));
    }

    @Test
    @DisplayName("Test update user functionality")
    public void givenUserDTO_whenUpdateUser_thenServiceIsCalledAndRedirect() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.times(1))
                .updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test update user with incorrect id functionality")
    public void givenIncorrectId_whenUpdateUser_thenRedirect() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.never())
                .updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test update user set duplicate login functionality")
    public void givenUserDTO_whenUpdateUserSetDuplicateLogin_thenErrorUpdateUserViewResponse() throws Exception {
        //given
        MyUser user = DataUtils.getSecondUserPersisted();
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        BDDMockito.given(userRepository.findByLogin(anyString())).willReturn(Optional.ofNullable(user));
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_LOGIN.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test update user set duplicate telegram functionality")
    public void givenUserDTO_whenUpdateUserSetDuplicateTelegram_thenErrorUpdateUserViewResponse() throws Exception {
        //given
        MyUser user = DataUtils.getSecondUserPersisted();
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        BDDMockito.given(userRepository.findByTelegram(anyString())).willReturn(Optional.ofNullable(user));
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_TELEGRAM.getMessage())));

        BDDMockito.verify(myUserService, BDDMockito.never()).updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test update user password functionality")
    public void givenUserDTO_whenUpdateUserPassword_thenServiceIsCalledAndRedirect() throws Exception {
        //given
        MyUser user = DataUtils.getFirstUserPersisted();
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.ofNullable(user));
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1/edit_password")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.times(1))
                .updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test update user password with incorrect id functionality")
    public void givenIncorrectId_whenUpdateUserPassword_thenRedirect() throws Exception {
        //given
        MyUserDTO dto = DataUtils.getFirstUserDTO();
        BDDMockito.given(userRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1/edit_password")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.never())
                .updateUser(anyInt(), any(MyUser.class), anyBoolean());
    }

    @Test
    @DisplayName("Test delete user functionality")
    public void givenId_whenDeleteUser_thenServiceIsCalledAndRedirect() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/administration/users/1"));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(myUserService, BDDMockito.times(1))
                .delete(anyInt());
    }

    @Test
    @DisplayName("Test delete telegram interactions functionality")
    public void givenDeleteTelegramInteractionsRequest_whenDeleteTelegramInteractions_thenServiceIsCalledAndRedirect()
            throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/administration/users/interactions"));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        BDDMockito.verify(telegramInteractionService, BDDMockito.times(1))
                .deleteAllTelegramInteractions();
    }

}