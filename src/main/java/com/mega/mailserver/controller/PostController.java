package com.mega.mailserver.controller;

import com.mega.mailserver.model.SecurityRole;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.exception.BadRequestException;
import com.mega.mailserver.service.PostService;
import com.mega.mailserver.service.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.AddressException;

@Secured(SecurityRole.USER)
@RestController
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final AuthService authService;

    @PostMapping
    public void send(@RequestBody Letter letter) {
        final User user = authService.getUser();
        try {
            postService.send(letter, user);
        } catch (AddressException | IllegalArgumentException e) {
            throw new BadRequestException("Wrong arguments");
        }
    }
}
