package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostService {

    private final SmtpService smtpService;
    private final MailboxService mailboxService;

    public void send(final Letter letter, final User user) {

        try {
            smtpService.send(letter, user);
        } catch(Exception e) {
            throw new RuntimeException("Cannot send message");
        }

        mailboxService.put(letter, user);
    }
}
