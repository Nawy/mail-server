package com.mega.mailserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    private String senderEmail;
    private String recipient;
    private String text;
    private LocalDateTime time;
}