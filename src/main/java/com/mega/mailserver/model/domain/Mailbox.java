package com.mega.mailserver.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("mailbox")
@Document(collection = "mailbox")
public class Mailbox {
    @Id
    private String userName;

    @Builder.Default
    private List<Chat> contacts = new ArrayList<>();

    @Builder.Default
    private List<Chat> spam = new ArrayList<>();
}
