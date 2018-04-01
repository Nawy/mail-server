package com.mega.mailserver.model.domain;

import com.mega.mailserver.model.enums.LetterDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("letter")
public class Letter {

    private String id;
    private String address;
    private List<String> cc;
    private List<String> bcc;
    private LetterDirection direction;
    private String text;
    private LocalDateTime time;
}