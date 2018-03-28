package com.mega.mailserver.model;

import lombok.Builder;
import lombok.Data;

import javax.mail.Address;
import java.util.List;

@Data
@Builder
public class ReceiveEmailDto {

    private List<Address> recipients;
    private String from;
    private String text;
}
