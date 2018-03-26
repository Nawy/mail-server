package com.mega.mailserver.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("userMessage")
@Document(collection = "userMessages")
public class UserMessages {
    @Id
    private String id;
    @Indexed
    private String ownerEmail;
    private Map<String, List<Message>> messages = new HashMap<>();
    private Map<String, List<Message>> spam = new HashMap<>();
}
