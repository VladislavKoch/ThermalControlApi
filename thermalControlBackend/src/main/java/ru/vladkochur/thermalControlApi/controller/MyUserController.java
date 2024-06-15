package ru.vladkochur.thermalControlApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vladkochur.thermalControlApi.dto.DtoConverter;
import ru.vladkochur.thermalControlApi.dto.MyUserDTO;
import ru.vladkochur.thermalControlApi.entity.MyUser;
import ru.vladkochur.thermalControlApi.service.TelegramInteractionServiceImpl;
import ru.vladkochur.thermalControlApi.service.securtyService.MyUserServiceImpl;
import ru.vladkochur.thermalControlApi.utils.UserValidator;

import java.time.LocalDateTime;


@Controller
@RequestMapping("/api/v1/administration/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Slf4j
public class MyUserController {
    private final MyUserServiceImpl myUserServiceImpl;
    private final DtoConverter converter;
    private final UserValidator userValidator;
    private final TelegramInteractionServiceImpl interactionService;

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("users", myUserServiceImpl.findAllUsers()
                .stream().map(converter::userToDto).toList());
        model.addAttribute("interactions", interactionService.getAllTelegramInteractions()
                .stream().map(converter::telegramInteractionToDto).toList());
        return "users/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", converter.userToDto(myUserServiceImpl.findUserById(id)));
        model.addAttribute("ID", id);
        return "users/show";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") MyUserDTO user) {
        return "users/new";
    }

    @PostMapping()
    public String createUser(@ModelAttribute("user") @Valid MyUserDTO myUserDTO, BindingResult bindingResult) {
        MyUser user = converter.dtoToUser(myUserDTO);
        if (user.getTelegram() == null || user.getTelegram().isBlank()){
            user.setTelegram(null);
        }
        userValidator.validate(user, bindingResult);
        userValidator.validateSave(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/new";
        }
        myUserServiceImpl.save(user);
        log.info(String.format("user created %s", LocalDateTime.now()));
        return "redirect:/api/v1/administration/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", converter.userToDto(myUserServiceImpl.findUserById(id)));
        model.addAttribute("ID", id);
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid MyUserDTO myUserDTO, BindingResult bindingResult,
                         Model model, @PathVariable("id") int id) {
        model.addAttribute("ID", id);
        MyUser user = converter.dtoToUser(myUserDTO);
        userValidator.validate(user, bindingResult);
        userValidator.validateUpdate(user, bindingResult, id);
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        myUserServiceImpl.updateUser(id, user, false);
        log.info(String.format("user updated %s", LocalDateTime.now()));
        return "redirect:/api/v1/administration/users";
    }

    @GetMapping("/{id}/edit_password")
    public String editPassword(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", converter.userToDto(myUserServiceImpl.findUserById(id)));
        model.addAttribute("ID", id);
        return "users/edit_password";
    }

    @PatchMapping("/{id}/edit_password")
    public String updatePassword(@ModelAttribute("user") @Valid MyUserDTO myUserDTO, BindingResult bindingResult,
                         Model model, @PathVariable("id") int id) {
        model.addAttribute("ID", id);
        MyUser user = converter.dtoToUser(myUserDTO);
        userValidator.validate(user, bindingResult);
        userValidator.validateUpdate(user, bindingResult, id);
        if (bindingResult.hasErrors()) {
            return "users/edit_password";
        }
        myUserServiceImpl.updateUser(id, user, true);
        log.info(String.format("user password updated %s", LocalDateTime.now()));
        return "redirect:/api/v1/administration/users";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        myUserServiceImpl.delete(id);
        log.info(String.format("user deleted %s", LocalDateTime.now()));
        return "redirect:/api/v1/administration/users";
    }

    @DeleteMapping("/interactions")
    public String deleteInteractions() {
        interactionService.deleteAllTelegramInteractions();
        log.info(String.format("telegram interactions deleted %s", LocalDateTime.now()));
        return "redirect:/api/v1/administration/users";
    }

}
