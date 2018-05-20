package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.Chat;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.Mailbox;
import com.mega.mailserver.repository.MailboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@AllArgsConstructor
public class MailboxService {

    private final MailboxRepository repository;

    public Collection<Letter> getChat(final String userName, final String address) {
        final Mailbox mailbox = getMailbox(userName);
        if (isNull(mailbox)) return Collections.emptyList();

        final Chat chat = getChatByAddress(address, mailbox.getContacts())
                .orElse(
                        getChatByAddress(address, mailbox.getSpam())
                                .orElse(null)
                );

        if(!Objects.isNull(chat)) {
            chat.setAmountNew(0);
            repository.save(mailbox);
        }

        return Objects.isNull(chat) ? Collections.emptyList() : chat.getMessages();
    }

    public List<Chat> getChats(final String userName, boolean isSpam) {
        final Mailbox mailbox = getMailbox(userName);
        if (isNull(mailbox)) return Collections.emptyList();
        return Optional.of(isSpam ? mailbox.getSpam() : mailbox.getContacts())
                .orElse(Collections.emptyList());
    }

    public Set<String> getChatNames(final String userName, final boolean spam) {
        final Mailbox mailbox = getMailbox(userName);
        if (isNull(mailbox)) return Collections.emptySet();
        return spam ?
                mailbox.getSpam()
                        .stream()
                        .map(Chat::getName)
                        .collect(Collectors.toSet())
                :
                mailbox.getContacts()
                        .stream()
                        .map(Chat::getName)
                        .collect(Collectors.toSet());
    }

    private Mailbox getMailbox(final String userName) {
        return repository.findById(userName).orElse(null);
    }

    public void put(final Letter letter, final String userName) {
        Objects.requireNonNull(letter);
        letter.setDeliveryTime(LocalDateTime.now());

        final Mailbox mailbox = putLetterToMailbox(letter, userName);

        repository.save(mailbox);
    }

    public Optional<Chat> getChatByAddress(String address, List<Chat> contacts) {
        return firstNonNull(contacts, new ArrayList<Chat>())
                .stream()
                .filter(c -> c.getName().equalsIgnoreCase(address))
                .findFirst();
    }

    public void setNotDelivered(final Letter letter, final String username) {
        Objects.requireNonNull(letter);

        final Mailbox mailbox = repository.findById(username)
                .orElseGet(() -> Mailbox.builder().userName(username).build());
        final String address = letter.getAddress().toLowerCase();
        final Chat contact = getChatByAddress(address, mailbox.getContacts())
                .orElse(null);

        if (!isNull(contact)) {
            findLetter(contact.getMessages(), letter.getId())
                    .ifPresent(dbLetter -> dbLetter.setNotDelivered(true));
        } else {
            final Chat spam = getChatByAddress(address, mailbox.getSpam())
                    .orElse(null);
            if (isNull(spam) || isEmpty(spam.getMessages())) return;
            Letter dbLetter = findLetter(spam.getMessages(), letter.getId()).orElse(null);
            if (dbLetter == null) return;
            dbLetter.setNotDelivered(true);
        }
        repository.save(mailbox);
    }

    public Mailbox putLetterToMailbox(final Letter letter, final String userName) {
        final Mailbox mailbox = repository.findById(userName)
                .orElseGet(() -> Mailbox.builder().userName(userName).build());

        final String address = letter.getAddress().toLowerCase();
        final Chat contact = getChatByAddress(address, mailbox.getContacts()).orElse(null);

        if (!isNull(contact)) {
            contact.getMessages().add(letter);
            contact.setLastDeliveryDate(letter.getDeliveryTime());
            contact.setAmountNew(contact.getAmountNew() + 1);
        } else {
            Chat spam = getChatByAddress(address, mailbox.getSpam())
                    .orElse(null);

            if (Objects.isNull(spam)) {
                spam = Chat.builder()
                        .name(address)
                        .amountNew(0)
                        .build();
                mailbox.getSpam().add(spam);
            }

            spam.getMessages().add(letter);
            spam.setLastDeliveryDate(letter.getDeliveryTime());
            spam.setAmountNew(spam.getAmountNew() + 1);
        }

        return mailbox;
    }

    private Optional<Letter> findLetter(List<Letter> letters, String id) {
        return letters.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }
}
