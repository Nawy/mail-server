package com.mega.mailserver.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SendEmailDto {

    private List<String> to;
    private String sender;
    private String fullName;
    private String text;
}
