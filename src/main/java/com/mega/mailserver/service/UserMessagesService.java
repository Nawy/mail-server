package com.mega.mailserver.service;

import com.mega.mailserver.model.Message;
import com.mega.mailserver.model.UserMessages;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.repository.UserMessagesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<Message> sendMessage(Message message) {
        String sender = message.getSenderEmail();
        String recipient = message.getRecipient();

        UserMessages userMessages = get(sender);
        userMessages.getMessages().add(recipient, message);
        userMessagesRepository.save(userMessages);
        return userMessages.getMessages().get(sender);
    }


}
