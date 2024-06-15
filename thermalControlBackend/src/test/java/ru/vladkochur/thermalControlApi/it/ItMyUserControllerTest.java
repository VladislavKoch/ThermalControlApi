package ru.vladkochur.thermalControlApi.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.MyUserDTO;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.it.testcontainers.AbstractRestControllerBaseTest;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;
import ru.vladkochur.thermalControlApi.repository.TelegramInteractionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.vladkochur.thermalControlApi.util.DataUtils.*;
import static ru.vladkochur.thermalControlApi.util.testConstraints.myUserControllerTestConstraints.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@WithMockUser(roles = {"ADMIN"})
class ItMyUserControllerTest extends AbstractRestControllerBaseTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DtoConverter converter;
    @Autowired
    private TelegramInteractionRepository interactionRepository;
    @Autowired
    private MyUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        interactionRepository.deleteAll();
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE my_user_id_seq RESTART WITH 1");
    }

    @Test
    @DisplayName("Test get all users functionality")
    public void givenThreeUsers_whenGetAllUsers_thenViewResponse() throws Exception {
        //given
        userRepository.save( getFirstUserTransient());
        userRepository.save( getSecondUserTransient());
        userRepository.save( getThirdUserTransient());
        interactionRepository.save( getFirstTelegramInteractionTransient());
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
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenUserId_whenShowUser_thenViewResponse() throws Exception {
        //given
        MyUser user = userRepository.save( getFirstUserTransient());
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
    }

    @Test
    @DisplayName("Test get user by incorrect id functionality")
    public void givenIncorrectUserId_whenShowUser_thenRedirect() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/administration/users/1"));
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
    public void givenUserDTO_whenSaveNewUser_thenRedirectAndUserIsSaved() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        MyUser user = converter.dtoToUser(dto);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test save new user with empty login functionality")
    public void givenUserDTOWithEmptyName_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setLogin(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_LOGIN.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test save new user with incorrect login functionality")
    public void givenUserDTOWithIncorrectName_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setLogin("Us");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_NAME.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test save new user with duplicate login functionality")
    public void givenUserDTOWithDuplicateLogin_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        userRepository.save( getFirstUserTransient());
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_LOGIN.getMessage())));

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test save new user with empty password functionality")
    public void givenUserDTOWithEmptyPassword_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setPassword(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_PASSWORD.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test save new user with incorrect password functionality")
    public void givenUserDTOWithIncorrectPassword_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setPassword("Ps");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_PASSWORD.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test save new user with duplicate telegram functionality")
    public void givenUserDTOWithDuplicateTelegram_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        MyUser user =  getFirstUserPersisted();
        user.setLogin("Login1");
        userRepository.save(user);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_TELEGRAM.getMessage())));

        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test save new user with empty roles functionality")
    public void givenUserDTOWithEmptyRoles_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setRoles(null);
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(EMPTY_ROLES.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test save new user with incorrect roles functionality")
    public void givenUserDTOWithIncorrectRoles_whenSaveNewUser_thenErrorNewUserViewResponse() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setRoles("INCORRECT_ROLE");
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/administration/users")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(INCORRECT_ROLES.getMessage())));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test get edit user form functionality")
    public void givenUser_whenGetEditUserForm_thenEditUserViewResponse() throws Exception {
        //given
        MyUser obtainedUser = userRepository.save( getFirstUserTransient());
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit",
                obtainedUser.getId())));
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
        MyUser obtainedUser = userRepository.save( getFirstUserTransient());
        //when
        ResultActions result = mockMvc.perform(get(String.format("/api/v1/administration/users/%s/edit_password",
                obtainedUser.getId())));
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
        MyUserDTO dto =  getFirstUserDTO();
        dto.setLogin("Login1");
        MyUser obtainedUser = userRepository.save( getFirstUserTransient());
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/" + obtainedUser.getId())
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(userRepository.findById(obtainedUser.getId())).isPresent();
        assertThat(userRepository.findById(obtainedUser.getId()).get().getLogin()).isEqualTo(dto.getLogin());
    }

    @Test
    @DisplayName("Test update user with incorrect id functionality")
    public void givenIncorrectId_whenUpdateUser_thenRedirect() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test update user set duplicate login functionality")
    public void givenUserDTO_whenUpdateUserSetDuplicateLogin_thenErrorUpdateUserViewResponse() throws Exception {
        //given
        userRepository.save( getFirstUserTransient());
        MyUser obtainedUser = userRepository.save( getSecondUserTransient());
        MyUserDTO dto =  getSecondUserDTO();
        String originName = dto.getLogin();
        dto.setLogin("user");
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/" + obtainedUser.getId())
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_LOGIN.getMessage())));

        assertThat(userRepository.findById(obtainedUser.getId()).get().getLogin()).isEqualTo(originName);
    }

    @Test
    @DisplayName("Test update user set duplicate telegram functionality")
    public void givenUserDTO_whenUpdateUserSetDuplicateTelegram_thenErrorUpdateUserViewResponse() throws Exception {
        //given
        MyUser duplicateOrigin = userRepository.save( getFirstUserTransient());
        MyUser obtainedUser = userRepository.save( getSecondUserTransient());
        MyUserDTO dto = converter.userToDto(obtainedUser);
        String originTelegram = dto.getTelegram();
        dto.setTelegram(duplicateOrigin.getTelegram());
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/" + obtainedUser.getId())
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DUPLICATE_TELEGRAM.getMessage())));

        assertThat(userRepository.findById(obtainedUser.getId()).get().getTelegram()).isEqualTo(originTelegram);
    }

    @Test
    @DisplayName("Test update user password functionality")
    public void givenUserDTO_whenUpdateUserPassword_thenServiceIsCalledAndRedirect() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        dto.setPassword("Password123");
        MyUser obtainedUser = userRepository.save( getFirstUserTransient());
        String originPassword = passwordEncoder.encode(obtainedUser.getPassword());
        //when
        ResultActions result = mockMvc.perform(patch(String.format("/api/v1/administration/users/%s/edit_password",
                obtainedUser.getId()))
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(userRepository.findById(obtainedUser.getId()).get().getPassword()).isNotEqualTo(originPassword);
    }

    @Test
    @DisplayName("Test update user password with incorrect id functionality")
    public void givenIncorrectId_whenUpdateUserPassword_thenRedirect() throws Exception {
        //given
        MyUserDTO dto =  getFirstUserDTO();
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/administration/users/1/edit_password")
                .flashAttr("user", dto));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));
    }

    @Test
    @DisplayName("Test delete user functionality")
    public void givenId_whenDeleteUser_thenServiceIsCalledAndRedirect() throws Exception {
        //given
        MyUser obtainedUser = userRepository.save( getFirstUserTransient());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/administration/users/" + obtainedUser.getId()));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Test delete telegram interactions functionality")
    public void givenDeleteTelegramInteractionsRequest_whenDeleteTelegramInteractions_thenServiceIsCalledAndRedirect()
            throws Exception {
        //given
        interactionRepository.save( getFirstTelegramInteractionTransient());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/administration/users/interactions"));
        //then
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/administration/users"));

        assertThat(interactionRepository.findAll()).isEmpty();
    }
}