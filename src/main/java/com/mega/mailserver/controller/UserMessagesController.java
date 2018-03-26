package com.mega.mailserver.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mega.mailserver.model.domain.Message;
import com.mega.mailserver.service.UserMessagesService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/messages")
public class UserMessagesController {

    private final UserMessagesService userMessagesService;

    @GetMapping("/{chatname}")
    public List<Message> get(@RequestParam("ownerEmail") String ownerEmail, @PathVariable("chatname") String chatname) {
        return userMessagesService.getChat(ownerEmail, chatname);
    }

    @PostMapping
    public void sendMessage(@RequestBody SendMessageDto messageDto) {
        userMessagesService.sendMessage(messageDto.toMessage());
    }

    @Getter
    private static class SendMessageDto {
        private String senderEmail;
        private String recipient;
        private String text;

        public SendMessageDto(@JsonProperty(value = "senderEmail", required = true) String senderEmail,
                              @JsonProperty(value = "recipient", required = true) String recipient,
                              @JsonProperty(value = "text", required = true) String text) {
            this.senderEmail = senderEmail;
            this.recipient = recipient;
            this.text = text;
        }

        Message toMessage() {
            return Message.builder()
                    .id(ObjectId.get().toHexString())
                    .senderEmail(senderEmail)
                    .recipient(recipient)
                    .text(text)
                    .time(LocalDateTime.now())
                    .build();
        }
    }

}
