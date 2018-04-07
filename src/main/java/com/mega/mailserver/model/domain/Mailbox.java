package com.mega.mailserver.model.domain;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("mailbox")
@Document(collection = "mailbox")
public class Mailbox {
    @Id
    private String id;
    @Indexed
    private String userName;

    @Builder.Default private Multimap<String, Letter> letters = HashMultimap.create();
    @Builder.Default private Multimap<String, Letter> spam = HashMultimap.create();
}
