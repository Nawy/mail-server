package com.mega.mailserver.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.Mailbox;
import com.mega.mailserver.repository.MailboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.MoreObjects.firstNonNull;

@Service
@AllArgsConstructor
public class MailboxService {

    private final MailboxRepository mailboxRepository;

    public Collection<Letter> getChat(final String userName, final String address) {
        final Mailbox mailbox = getMailbox(userName);
        if (Objects.isNull(mailbox)) return Collections.emptyList();
        final Collection<Letter> conversation = mailbox.getLetters().get(address);
        return conversation.isEmpty() ? mailbox.getSpam().get(address) : conversation;
    }

    public Set<String> getChatNames(final String userName, final boolean spam){
        final Mailbox mailbox = getMailbox(userName);
        if (Objects.isNull(mailbox)) return Collections.emptySet();
        return spam? mailbox.getSpam().keySet(): mailbox.getLetters().keySet();
    }

    private Mailbox getMailbox(final String userName) {
        return mailboxRepository.findById(userName).orElse(null);
    }

    public Collection<Letter> getLetters(final String userName) {
        final Mailbox mailbox = getMailbox(userName);
        return Objects.isNull(mailbox) ? Collections.emptyList() : mailbox.getLetters().values();
    }

    public Collection<Letter> getSpam(final String userName) {
        final Mailbox mailbox = getMailbox(userName);
        return Objects.isNull(mailbox) ? Collections.emptyList() : mailbox.getSpam().values();
    }

    public void put(final Letter letter, final String userName) {
        Objects.requireNonNull(letter);
        letter.setTime(LocalDateTime.now());

        final Mailbox mailbox = mailboxRepository.findById(userName)
                .orElseGet(() -> Mailbox.builder().userName(userName).build());

        final String address = letter.getAddress().toLowerCase();
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

    public void setNotDelivered(final Letter letter, final String username) {
        Objects.requireNonNull(letter);

        final Mailbox mailbox = mailboxRepository.findById(username)
                .orElseGet(() -> Mailbox.builder().userName(username).build());
        final String address = letter.getAddress().toLowerCase();
        final Multimap<String, Letter> letters = mailbox.getLetters();
        if (letters.containsKey(address)) {
            Letter dbLetter = findLetter(letters, address, letter.getId()).orElse(null);
            if (dbLetter != null) {
                dbLetter.setNotDelivered(true);
                mailbox.setLetters(letters);
            }
        } else {
            final Multimap<String, Letter> spam = mailbox.getSpam();
            if (spam.isEmpty()) return;
            Letter dbLetter = findLetter(spam, address, letter.getId()).orElse(null);
            if (dbLetter == null) return;
            dbLetter.setNotDelivered(true);
            mailbox.setSpam(spam);
        }
        mailboxRepository.save(mailbox);
    }

    private Optional<Letter> findLetter(Multimap<String, Letter> letters, String address, String id) {
        return letters.get(address).stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }
}
