package com.mega.mailserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.MultiValueMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "userMessages")
public class UserMessages {
    @Id
    private String id;
    @Indexed
    private String ownerEmail;
    private MultiValueMap<String, Message> messages;
    private MultiValueMap<String, Message> spam;
}
