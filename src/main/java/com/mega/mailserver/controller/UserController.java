package com.mega.mailserver.controller;

import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.dto.UserDto;
import com.mega.mailserver.model.exception.BadRequestException;
import com.mega.mailserver.model.exception.ForbiddenException;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.service.UserService;
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

    @PutMapping
    public UserDto insert(@RequestBody User user) {
        final User existedUser = userService.get(user.getName());

        if (Objects.isNull(existedUser)) {
            throw new ForbiddenException(String.format("user with such email :'%s' already exists", user.getName()));
        }
        final User resultUser = userService.upsert(user);
        return UserDto.valueOf(resultUser);
    }

    @PostMapping
    public UserDto update(@RequestBody User user) {
        final User existedUser = userService.get(user.getName());

        if (Objects.isNull(existedUser)) {
            throw new NotFoundException("No user found for id: " + user.getId());
        }
        final User resultUser = userService.upsert(user);
        return UserDto.valueOf(resultUser);
    }

    @GetMapping("/{name}")
    public UserDto get(@PathVariable("name") String name) {
        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Empty request");
        }

        final User user = userService.get(name);

        if (Objects.isNull(user)) {
            throw new NotFoundException("cannot find user with name " + name);
        }

        return UserDto.valueOf(user);
    }
}
