package com.mega.mailserver.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("message")
public class Message {

    private String id;
    private String senderEmail;
    private String recipient;
    private String text;
    private LocalDateTime time;
}