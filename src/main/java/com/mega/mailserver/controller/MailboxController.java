package com.mega.mailserver.controller;

import com.mega.mailserver.model.SecurityRole;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.exception.BadRequestException;
import com.mega.mailserver.service.MailboxService;
import com.mega.mailserver.service.PostService;
import com.mega.mailserver.service.security.AuthService;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
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

    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    private final PostService postService;
    private final MailboxService mailboxService;
    private final AuthService authService;

    @Secured(SecurityRole.USER)
    @PostMapping
    public void addLetter(@RequestBody Letter letter) {

        final boolean isValidEmail = emailValidator.isValid(letter.getAddress());
        if (!isValidEmail) throw new BadRequestException("bad email set");

        final User user = authService.getUser();
        try {
            postService.send(letter, user);
        } catch (AddressException | IllegalArgumentException e) {
            throw new BadRequestException("Wrong arguments");
        }
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/{address}")
    public Collection<Letter> getChat(@PathVariable("address") String address) {
        final User user = authService.getUser();
        return mailboxService.getChat(user.getName(), address);
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/chat/names")
    public Set<String> getChatNames() {
        final User user = authService.getUser();
        return mailboxService.getChatNames(user.getName(), false);
    }

    @Secured(SecurityRole.USER)
    @GetMapping("/spam/chat/names")
    public Set<String> getSpamChatNames() {
        final User user = authService.getUser();
        return mailboxService.getChatNames(user.getName(), true);
    }
}
