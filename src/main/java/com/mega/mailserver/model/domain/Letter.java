package com.mega.mailserver.model.domain;

import com.mega.mailserver.model.enums.LetterDirection;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TypeAlias("letter")
public class Letter {

    @Builder.Default
    private String id = new ObjectId().toString();
    private String address;
    private List<String> cc;
    private List<String> bcc;
    private LetterDirection direction;
    private String text;
    private String htmlText;
    private LocalDateTime seenTime;
    private LocalDateTime deliveryTime;
    private Boolean notDelivered;
}