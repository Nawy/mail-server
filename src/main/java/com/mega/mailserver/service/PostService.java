package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailProperties;
import com.mega.mailserver.model.EmailAddress;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.enums.LetterDirection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostService {

    private final SmtpService smtpService;
    private final MailboxService mailboxService;
    private final EmailProperties emailProperties;

    public void send(final Letter letter, final User user) throws AddressException {

        final EmailAddress fromAddress = new EmailAddress(user.getName(), emailProperties.getDomain());
        final EmailAddress toAddress = new EmailAddress(letter.getAddress());

        final String domain = toAddress.getDomain();
        if (!isLocalDomain(domain)) {
            try {
                smtpService.send(letter, user);
            } catch (Exception e) {
                throw new RuntimeException("Cannot send message");
            }
        }

        final Letter inboxEmail = letter.toBuilder()
                .address(fromAddress.getAddress().getAddress())
                .direction(LetterDirection.INBOX)
                .build();

        final Letter outboxEmail = letter.toBuilder()
                .direction(LetterDirection.OUTBOX)
                .build();

        mailboxService.put(inboxEmail, toAddress.getName());
        mailboxService.put(outboxEmail, user.getName());
    }

    private boolean isLocalDomain(final String domain) {
        return domain.equalsIgnoreCase(emailProperties.getDomain());
    }
}
