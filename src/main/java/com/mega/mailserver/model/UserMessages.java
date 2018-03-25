package com.mega.mailserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessages {
    @Id
    private String id;
    private String ownerEmail;
    private Map<String, List<Message>> messages;
    private Map<String, List<Message>> spam;
}
