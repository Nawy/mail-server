package com.mega.mailserver.model.dto;

import com.mega.mailserver.model.domain.Chat;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String name;
    private LocalDateTime lastDeliveryDate;
    private int amountNew;

    public static ChatDto valueOf(Chat chat) {
        return ChatDto.builder()
                .name(chat.getName())
                .amountNew(chat.getAmountNew())
                .lastDeliveryDate(chat.getLastDeliveryDate())
                .build();
    }
}
