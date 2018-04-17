package com.mega.mailserver.controller;

import com.mega.mailserver.model.SecurityRole;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.exception.BadRequestException;
import com.mega.mailserver.service.MailboxService;
import com.mega.mailserver.service.PostService;
import com.mega.mailserver.service.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import java.util.Collection;
import java.util.Set;

@Secured(SecurityRole.USER)
@RestController
@AllArgsConstructor
@RequestMapping("/mailbox")
public class MailboxController {

    private final PostService postService;
    private final MailboxService mailboxService;
    private final AuthService authService;

    @Secured(SecurityRole.USER)
    @PostMapping
    public void addLetter(@RequestBody Letter letter) {
        final User user = authService.getUser();
        try {
            postService.send(letter, user);
        } catch (AddressException | IllegalArgumentException e) {
            throw new BadRequestException("Wrong arguments");
        }
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/{address}")
    public Collection<Letter> getConversation(@PathVariable("address") String address) {
        final User user = authService.getUser();
        return mailboxService.getConversation(user.getName(), address);
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/letters")
    public Collection<Letter> getLetters() {
        final User user = authService.getUser();
        return mailboxService.getLetters(user.getName());
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/spam")
    public Collection<Letter> getSpam() {
        final User user = authService.getUser();
        return mailboxService.getSpam(user.getName());
    }
}
