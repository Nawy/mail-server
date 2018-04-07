package com.mega.mailserver.model;

import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.enums.LetterDirection;
import lombok.Builder;
import lombok.Data;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReceiveEmailDto {

    private List<InternetAddress> recipients;
    private List<InternetAddress> cc;
    private List<InternetAddress> bcc;
    private String from;
    private String text;

    public Letter toLetter() {
        return Letter.builder()
                .address(from)
                .text(text)
                .direction(LetterDirection.INBOX)
                .build();
    }
}
