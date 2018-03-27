package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.Message;
import com.mega.mailserver.model.domain.UserMessages;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.repository.UserMessagesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserMessagesService {

    private final UserMessagesRepository userMessagesRepository;

    public List<Message> getChat(String ownerEmail, String chatname) {
        UserMessages userMessages = get(ownerEmail);
        return userMessages.getMessages().getOrDefault(chatname, Collections.emptyList());
    }

    public UserMessages get(String ownerEmail) {
        return userMessagesRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new NotFoundException("no user messages for email: " + ownerEmail));
    }

    public void sendMessage(Message message) {
        addMessage(MessageType.INBOX, message);
        addMessage(MessageType.OUTBOX, message);
    }

    private void addMessage(MessageType messageType, Message message) {
        String ownerEmail = messageType == MessageType.INBOX ? message.getRecipient() : message.getSenderEmail();
        String chatName = messageType == MessageType.INBOX ? message.getSenderEmail() : message.getRecipient();
        userMessagesRepository
                .findByOwnerEmail(ownerEmail)
                .ifPresent(userMessages -> {
                    List<Message> messages = userMessages.getMessages().getOrDefault(chatName, new ArrayList<>());
                    messages.add(message);
                    userMessages.getMessages().put(chatName, messages);
                    userMessagesRepository.save(userMessages);
                });
    }

    public UserMessages create(String ownerEmail) {
        return userMessagesRepository.save(
                UserMessages.builder().ownerEmail(ownerEmail).build()
        );
    }

    public enum MessageType {
        INBOX, OUTBOX
    }

}
