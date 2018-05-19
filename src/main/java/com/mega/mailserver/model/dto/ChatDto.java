package com.mega.mailserver.model.dto;

import com.mega.mailserver.model.domain.Letter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String name;
    private LocalDateTime lastDeliveryDate;
    private int amountNew;
    private List<Letter> messages;
}
