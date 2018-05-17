package com.mega.mailserver.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatNameDto {
    private String name;
    private LocalDateTime lastDeliveryDate;
    private int amountNew;
}
