package com.mega.mailserver.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.Mailbox;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.repository.MailboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;

@Service
@AllArgsConstructor
public class MailboxService {

    private final UserService userService;
    private final MailboxRepository mailboxRepository;

    public Collection<Letter> getConversation(final String userName, final String address) {

        final Mailbox mailbox = mailboxRepository.findById(userName).orElseGet(null);

        Collection<Letter> conversation = mailbox.getLetters().get(address);

        if(CollectionUtils.isEmpty(conversation)) {
            conversation = mailbox.getSpam().get(address);
        }

        return Objects.isNull(conversation) ? Collections.emptyList() : conversation;
    }

    public Collection<Letter> getLetters(final String userName) {
        final Mailbox mailbox = mailboxRepository.findById(userName).orElseGet(null);
        return Objects.isNull(mailbox) ? Collections.emptyList() : mailbox.getLetters().values();
    }

    public Collection<Letter> getSpam(final String userName) {
        final Mailbox mailbox = mailboxRepository.findById(userName).orElseGet(null);
        return Objects.isNull(mailbox) ? Collections.emptyList() : mailbox.getSpam().values();
    }

    public void put(final Letter letter, final String userName) {
        Objects.requireNonNull(letter);

        final Mailbox mailbox = mailboxRepository.findById(userName)
                .orElseGet(() -> Mailbox.builder().userName(userName).build());

        final String address = letter.getAddress();

        final Multimap<String, Letter> letters = firstNonNull(mailbox.getLetters(), HashMultimap.create());

        if (letters.containsKey(address)) {
            letters.put(address, letter);
            mailbox.setLetters(letters);
        } else {
            final Multimap<String, Letter> spam = firstNonNull(mailbox.getSpam(), HashMultimap.create());
            spam.put(address, letter);
            mailbox.setSpam(spam);
        }

        mailboxRepository.save(mailbox);
    }
}
