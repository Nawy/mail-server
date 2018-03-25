package com.mega.mailserver.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mega.mailserver.model.User;
import com.mega.mailserver.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/post")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User save(@RequestBody UserCreateDto userCreateDto) {
        return userService.save(userCreateDto.toUser());
    }

    @GetMapping("/{email}")
    public User get(@PathVariable("email") String email) {
        return userService.get(email);
    }

    @Getter
    private static class UserCreateDto {
        private String email;
        private String password;

        public UserCreateDto(@JsonProperty(value = "email", required = true) String email,
                             @JsonProperty(value = "password", required = true) String password) {
            this.email = email;
            this.password = password;
        }

        User toUser() {
            return User.builder().email(email).password(password).build();
        }
    }
}
