package com.mega.mailserver.controller;

import com.mega.mailserver.model.SecurityRole;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.dto.UserDto;
import com.mega.mailserver.model.exception.BadRequestException;
import com.mega.mailserver.model.exception.ForbiddenException;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.service.UserService;
import com.mega.mailserver.service.security.AuthService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping
    public UserDto insert(@RequestBody UserDto user) {
        if(StringUtils.isBlank(user.getName())) {
            throw new BadRequestException("Name is empty");
        }
        final User existedUser = userService.get(user.getName());

        if (Objects.nonNull(existedUser)) {
            throw new ForbiddenException(String.format("user with such email :'%s' already exists", user.getName()));
        }
        final User resultUser = userService.upsert(user.toUser());
        return UserDto.valueOf(resultUser);
    }

    @Secured(SecurityRole.USER)
    @PutMapping
    public UserDto update(@RequestBody UserDto user) {
        final User existedUser = userService.get(user.getName());

        if (Objects.isNull(existedUser)) {
            throw new NotFoundException("No user found for name: " + user.getName());
        }
        final User resultUser = userService.upsert(user.toUser());
        return UserDto.valueOf(resultUser);
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/{name}")
    public UserDto getInfo(@PathVariable("name") String name) {
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Empty request");
        }

        final User user = userService.get(name);

        if (Objects.isNull(user)) {
            throw new NotFoundException("cannot find user with name " + name);
        }

        return UserDto.valueOf(user);
    }

    @Secured(SecurityRole.USER)
    @GetMapping
    public String getSessionUserName(){
        final User user = authService.getUser();
        return user.getName();
    }

    @GetMapping("/{name}/name")
    public UserDto get(@PathVariable("name") String name) {
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Empty request");
        }

        final User user = userService.get(name);

        if (Objects.isNull(user)) {
            throw new NotFoundException("cannot find user with name " + name);
        }

        return UserDto.nameValueOf(user);
    }
}
