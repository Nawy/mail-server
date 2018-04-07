package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.Mailbox;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.repository.MailboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;

@Service
@AllArgsConstructor
public class MailboxService {

    private final UserService userService;
    private final MailboxRepository mailboxRepository;

    public Collection<Letter> getConversation(final User user, final String address) {

        final Mailbox mailbox = user.getMailbox();

        Collection<Letter> conversation = mailbox.getLetters().get(address);

        if(Objects.isNull(conversation)) {
            conversation = mailbox.getSpam().get(address);
        }

        return Objects.isNull(conversation) ? Collections.emptyList() : conversation;
    }

    public Collection<Letter> getLetters(final User user) {
        return user.getMailbox().getLetters().values();
    }

    public Collection<Letter> getSpam(final User user) {
        return user.getMailbox().getSpam().values();
    }

    public void put(final Letter letter, final User user) {
        Objects.requireNonNull(letter);

        final Mailbox mailbox = firstNonNull(
                user.getMailbox(),
                Mailbox.builder()
                        .userName(user.getName())
                        .build()
        );
        final String address = letter.getAddress();

        if (mailbox.getLetters().containsKey(address)) {
            mailbox.getLetters().put(address, letter);
        } else {
            mailbox.getSpam().put(address, letter);
        }

        mailboxRepository.save(mailbox);
        user.setMailbox(mailbox);
        userService.save(user);
    }
}
