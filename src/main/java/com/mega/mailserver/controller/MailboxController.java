package com.mega.mailserver.controller;

import com.mega.mailserver.model.SecurityRole;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.service.MailboxService;
import com.mega.mailserver.service.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Secured(SecurityRole.USER)
@RestController
@AllArgsConstructor
@RequestMapping("/mailbox")
public class MailboxController {

    private final MailboxService mailboxService;
    private final AuthService authService;

    @GetMapping("/{address}")
    public Collection<Letter> getConversation(@PathVariable("address") String address) {
        final User user = authService.getUser();
        return mailboxService.getConversation(user, address);
    }

    @GetMapping("/letters")
    public Collection<Letter> getLetters() {
        final User user = authService.getUser();
        return mailboxService.getLetters(user);
    }

    @GetMapping("/spam")
    public Collection<Letter> getSpam() {
        final User user = authService.getUser();
        return mailboxService.getSpam(user);
    }
}
