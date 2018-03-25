package com.mega.mailserver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiveEmailDto {

    private String recipient;
    private String from;
    private String text;
}
